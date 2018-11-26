package com.cmatch.persistence;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cmatch.domain.ChatMessage;
import com.cmatch.domain.ChatRoom;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    
    @Query("SELECT m FROM ChatMessage m  WHERE m.room = :room")
    public List<ChatMessage> findByRoom(@Param("room") ChatRoom room, Pageable pageable);
    
    @Query("SELECT m1 FROM ChatMessage m1, ChatMessage m2 "
         + "WHERE m2.id = :messageId "
           + "AND m1.room = :room "
           + "AND m1.writeTime < m2.writeTime")
    public List<ChatMessage> findByRoomAndComparingTime(@Param("messageId") Integer messageId           
                                                      , @Param("room") ChatRoom room
                                                      , Pageable pageable);
}
