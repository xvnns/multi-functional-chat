package com.example.multifunctionalchat.repository;

import com.example.multifunctionalchat.domain.Chat;
import com.example.multifunctionalchat.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    boolean existsById(Long id);

    List<Message> findAllByChatOrderById(Chat chat);
}
