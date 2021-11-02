package com.example.multifunctionalchat.repository;

import com.example.multifunctionalchat.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    boolean existsChatByName(String name);
    Chat getChatByName(String name);
    @Query("select u.chats from Chat c join c.users u where u.id= :id")
    List<Chat> getChatListByUserId(@Param("id") Long id);
}
