package com.example.multifunctionalchat.repository;

import com.example.multifunctionalchat.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    boolean existsById(Long id);
}
