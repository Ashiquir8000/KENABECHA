package com.example.catalogproject.repository;

import com.example.catalogproject.entity.ChatMessage;
import com.example.catalogproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {

    List<ChatMessage> findByUserOrderByTimestampAsc(User user);

    @Query("SELECT DISTINCT m.user FROM ChatMessage m")
    List<User> findDistinctChatUsers();

    long countByUserAndIsReadFalseAndSenderType(User user, String senderType);
}