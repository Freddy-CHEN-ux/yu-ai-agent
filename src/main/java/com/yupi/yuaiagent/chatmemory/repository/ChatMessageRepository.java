package com.yupi.yuaiagent.chatmemory.repository;

import com.yupi.yuaiagent.chatmemory.entity.ChatMessageEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 聊天消息存储库
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    /**
     * 根据会话ID查询消息，按序号排序
     */
    List<ChatMessageEntity> findByConversationIdOrderBySequenceNumber(String conversationId);

    /**
     * 根据会话ID查询最新的N条消息
     */
    @Query("SELECT c FROM ChatMessageEntity c WHERE c.conversationId = :conversationId " +
           "ORDER BY c.sequenceNumber DESC")
    List<ChatMessageEntity> findTopNByConversationId(@Param("conversationId") String conversationId, 
                                                      Pageable pageable);

    /**
     * 获取某个会话的最大序号
     */
    @Query("SELECT COALESCE(MAX(c.sequenceNumber), 0) FROM ChatMessageEntity c WHERE c.conversationId = :conversationId")
    Integer getMaxSequenceNumber(@Param("conversationId") String conversationId);

    /**
     * 根据会话ID删除所有消息
     */
    @Modifying
    @Query("DELETE FROM ChatMessageEntity c WHERE c.conversationId = :conversationId")
    void deleteByConversationId(@Param("conversationId") String conversationId);
}