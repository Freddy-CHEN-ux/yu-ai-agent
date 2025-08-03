package com.yupi.yuaiagent.app;

import com.yupi.yuaiagent.advisor.MyLoggerAdvisor;
import com.yupi.yuaiagent.advisor.ReReadingAdvisor;
import com.yupi.yuaiagent.chatmemory.FileBasedChatMemory;
import com.yupi.yuaiagent.chatmemory.MySQLChatMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class LoveApp {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    public LoveApp(ChatModel dashscopeChatModel, 
                   MySQLChatMemory mySQLChatMemory,
                   @Value("${app.chat.memory.type:mysql}") String memoryType) {
        
        // 选择对话记忆实现方式
        ChatMemory chatMemory = chooseChatMemory(mySQLChatMemory, memoryType);
        
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        new MyLoggerAdvisor()
//                        new ReReadingAdvisor()
                )
                .build();
    }

    /**
     * 根据配置选择 ChatMemory 实现
     */
    private ChatMemory chooseChatMemory(MySQLChatMemory mySQLChatMemory, String memoryType) {
        ChatMemory chatMemory;
        try {
            switch (memoryType.toLowerCase()) {
                case "mysql" -> {
                    chatMemory = mySQLChatMemory;
                    log.info("Using MySQL ChatMemory for conversation storage");
                }
                case "file" -> {
                    String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
                    chatMemory = new FileBasedChatMemory(fileDir);
                    log.info("Using File-based ChatMemory for conversation storage: {}", fileDir);
                }
                case "memory" -> {
                    chatMemory = new InMemoryChatMemory();
                    log.info("Using In-Memory ChatMemory for conversation storage");
                }
                default -> {
                    log.warn("Unknown memory type: {}, falling back to MySQL", memoryType);
                    chatMemory = mySQLChatMemory;
                }
            }
        } catch (Exception e) {
            log.error("Failed to initialize {} ChatMemory, falling back to FileBasedChatMemory: {}", 
                      memoryType, e.getMessage());
            String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
            chatMemory = new FileBasedChatMemory(fileDir);
        }
        return chatMemory;
    }

    public String doChat(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    record LoveReport(String title, List<String> suggestions) {
    }

    public LoveReport doChatWithReport(String message, String chatId) {
        LoveReport loveReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(LoveReport.class);
        log.info("loveReport: {}", loveReport);
        return loveReport;
    }


}

