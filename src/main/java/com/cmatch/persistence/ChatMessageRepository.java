package com.cmatch.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cmatch.domain.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {

}
