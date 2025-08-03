-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS yu_ai_agent
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE yu_ai_agent;

-- 创建聊天消息表
CREATE TABLE IF NOT EXISTS chat_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    conversation_id VARCHAR(100) NOT NULL COMMENT '会话ID',
    content TEXT NOT NULL COMMENT '消息内容',
    message_type VARCHAR(20) NOT NULL COMMENT '消息类型：user, assistant, system',
    sequence_number INT NOT NULL COMMENT '消息序号（在会话中的顺序）',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    properties TEXT COMMENT '额外的消息属性（JSON 格式存储）',
    
    -- 索引
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_conversation_sequence (conversation_id, sequence_number),
    INDEX idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天消息表';