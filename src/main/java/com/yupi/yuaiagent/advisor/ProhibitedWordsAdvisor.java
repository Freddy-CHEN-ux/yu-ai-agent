package com.yupi.yuaiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.MessageAggregator;
import org.springframework.core.io.ClassPathResource;
import reactor.core.publisher.Flux;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class ProhibitedWordsAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    private Set<String> prohibitedWords = new HashSet<>();
    private static final String PROHIBITED_WORDS_FILE = "prohibited/prohibited-words.txt";

    @PostConstruct
    public void init() {
        loadProhibitedWords();
        log.info("ProhibitedWordsAdvisor initialized with {} prohibited words", prohibitedWords.size());
    }

    private void loadProhibitedWords() {
        try {
            ClassPathResource resource = new ClassPathResource(PROHIBITED_WORDS_FILE);
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String word = line.trim();
                    if (!word.isEmpty()) {
                        prohibitedWords.add(word.toLowerCase());
                    }
                }
            }
        } catch (IOException e) {
            log.error("Failed to load prohibited words from {}", PROHIBITED_WORDS_FILE, e);
            throw new RuntimeException("Failed to initialize prohibited words", e);
        }
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return -100;
    }

    private AdvisedRequest before(AdvisedRequest request) {
        String userText = request.userText();
        if (userText == null || userText.isEmpty()) {
            return request;
        }

        log.debug("Checking user input for prohibited words: {}", userText);
        
        String lowerCaseText = userText.toLowerCase();
        for (String prohibitedWord : prohibitedWords) {
            if (lowerCaseText.contains(prohibitedWord)) {
                log.warn("Prohibited word detected in user input: '{}' contains '{}'", userText, prohibitedWord);
                throw new ProhibitedWordException("用户输入包含违禁词，请重新输入合适的内容。");
            }
        }

        return request;
    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        advisedRequest = this.before(advisedRequest);
        return chain.nextAroundCall(advisedRequest);
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        advisedRequest = this.before(advisedRequest);
        return chain.nextAroundStream(advisedRequest);
    }

    public static class ProhibitedWordException extends RuntimeException {
        public ProhibitedWordException(String message) {
            super(message);
        }
    }
}