package com.yupi.yuaiagent.chatmemory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yupi.yuaiagent.chatmemory.entity.ChatMessageEntity;
import com.yupi.yuaiagent.chatmemory.repository.ChatMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.model.Media;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 基于 MySQL 数据库的对话记忆实现
 */
@Component
@Slf4j
public class MySQLChatMemory implements ChatMemory {

    private final ChatMessageRepository chatMessageRepository;
    private final ObjectMapper objectMapper;

    public MySQLChatMemory(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    @Transactional
    public void add(String conversationId, List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }

        // 获取当前会话的最大序号
        Integer maxSequence = chatMessageRepository.getMaxSequenceNumber(conversationId);
        
        List<ChatMessageEntity> entities = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            Message message = messages.get(i);
            ChatMessageEntity entity = convertToEntity(conversationId, message, maxSequence + i + 1);
            entities.add(entity);
        }
        
        chatMessageRepository.saveAll(entities);
        log.debug("Saved {} messages for conversation: {}", entities.size(), conversationId);
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        if (lastN <= 0) {
            return Collections.emptyList();
        }

        // 查询最新的 N 条消息
        List<ChatMessageEntity> entities = chatMessageRepository.findTopNByConversationId(
                conversationId, PageRequest.of(0, lastN));
        
        // 转换为 Message 对象并按序号正序排列
        List<Message> messages = new ArrayList<>();
        Collections.reverse(entities); // 因为查询是按序号倒序，需要反转
        
        for (ChatMessageEntity entity : entities) {
            Message message = convertToMessage(entity);
            if (message != null) {
                messages.add(message);
            }
        }
        
        log.debug("Retrieved {} messages for conversation: {}", messages.size(), conversationId);
        return messages;
    }

    @Override
    @Transactional
    public void clear(String conversationId) {
        chatMessageRepository.deleteByConversationId(conversationId);
        log.debug("Cleared all messages for conversation: {}", conversationId);
    }

    /**
     * 将 Spring AI Message 转换为数据库实体
     */
    private ChatMessageEntity convertToEntity(String conversationId, Message message, int sequenceNumber) {
        ChatMessageEntity entity = new ChatMessageEntity();
        entity.setConversationId(conversationId);
        entity.setContent(message.getText());
        entity.setSequenceNumber(sequenceNumber);
        
        // 根据消息类型设置 messageType
        if (message instanceof UserMessage) {
            entity.setMessageType("user");
        } else if (message instanceof AssistantMessage) {
            entity.setMessageType("assistant");
        } else if (message instanceof SystemMessage) {
            entity.setMessageType("system");
        } else {
            entity.setMessageType("unknown");
        }
        
        // 保存消息的其他属性（如果有的话）
        try {
            Map<String, Object> properties = message.getMetadata();
            if (properties != null && !properties.isEmpty()) {
                entity.setProperties(objectMapper.writeValueAsString(properties));
            }
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize message properties: {}", e.getMessage());
        }
        
        return entity;
    }

    /**
     * 将数据库实体转换为 Spring AI Message
     */
    private Message convertToMessage(ChatMessageEntity entity) {
        try {
            // 对于简单的文本消息，我们只需要内容，不需要复杂的属性
            return switch (entity.getMessageType()) {
                case "user" -> new UserMessage(entity.getContent());
                case "assistant" -> new AssistantMessage(entity.getContent());
                case "system" -> new SystemMessage(entity.getContent());
                default -> {
                    log.warn("Unknown message type: {}, treating as UserMessage", entity.getMessageType());
                    yield new UserMessage(entity.getContent());
                }
            };
        } catch (Exception e) {
            log.warn("Failed to convert message: {}", e.getMessage());
            // 如果转换失败，默认创建 UserMessage
            return new UserMessage(entity.getContent());
        }
    }
}