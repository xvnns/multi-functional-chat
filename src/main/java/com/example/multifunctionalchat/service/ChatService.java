package com.example.multifunctionalchat.service;

import com.example.multifunctionalchat.domain.Chat;

import java.util.List;

public interface ChatService {
    Chat add(Chat chat);
    void delete(Chat chat);
    Chat getById(Long id);
    List<Chat> getAll();
}
