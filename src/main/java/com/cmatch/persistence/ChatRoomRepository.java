package com.cmatch.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cmatch.domain.ChatRoom;
import com.cmatch.domain.User;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
    @Query("SELECT r FROM ChatRoom r  WHERE r.user1 = :user OR r.user2 = :user")
    public List<ChatRoom> findByRoomUser(@Param("user") User user);

    @Query("SELECT r FROM ChatRoom r WHERE r.name = :roomName")
    public ChatRoom FindByName(@Param("roomName") String roomName);
}
