package com.example.multifunctionalchat.repository;

import com.example.multifunctionalchat.domain.Chat;
import com.example.multifunctionalchat.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String login);
    boolean existsByUsername(String login);
    void deleteById(Long id);
    @Query("select c.users from User u join u.chats c where c.id= :id")
    List<User> getUserListByChatId(@Param("id") Long id);
}
