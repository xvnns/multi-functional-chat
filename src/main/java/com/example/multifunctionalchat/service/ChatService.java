package com.example.multifunctionalchat.service;

import com.example.multifunctionalchat.domain.Chat;
import com.example.multifunctionalchat.exception.AddingToTheDatabaseException;
import com.example.multifunctionalchat.exception.ChatNotFoundException;
import com.example.multifunctionalchat.exception.DeleteFromDatabaseException;

import java.util.List;

public interface ChatService {
    void save(Chat chat) throws AddingToTheDatabaseException;
    void delete(Chat chat);
    Chat getById(Long id) throws IllegalArgumentException;
    List<Chat> getAll();
    Chat getByName(String name) throws ChatNotFoundException;
    void addUser(Chat chat, Long userId) throws AddingToTheDatabaseException;
    void deleteUser(Chat chat, Long userId) throws DeleteFromDatabaseException;
    void update(Chat chat) throws AddingToTheDatabaseException;
}
