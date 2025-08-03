package com.yupi.yuaiagent.chatmemory.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 聊天消息实体类
 */
@Entity
@Table(name = "chat_message")
@Data
public class ChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 会话ID
     */
    @Column(nullable = false, length = 100)
    private String conversationId;

    /**
     * 消息内容
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 消息类型：user, assistant, system
     */
    @Column(nullable = false, length = 20)
    private String messageType;

    /**
     * 消息序号（在会话中的顺序）
     */
    @Column(nullable = false)
    private Integer sequenceNumber;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdTime;

    /**
     * 额外的消息属性（JSON 格式存储）
     */
    @Column(columnDefinition = "TEXT")
    private String properties;
}