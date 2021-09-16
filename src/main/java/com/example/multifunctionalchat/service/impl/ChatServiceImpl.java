package com.example.multifunctionalchat.service.impl;

import com.example.multifunctionalchat.domain.Chat;
import com.example.multifunctionalchat.repository.ChatRepository;
import com.example.multifunctionalchat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    @Autowired
    public ChatServiceImpl(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Transactional
    public Chat add(Chat chat) {
        return chatRepository.saveAndFlush(chat);
    }

    @Transactional
    public void delete(Chat chat) {
        chatRepository.delete(chat);
    }

    @Transactional(readOnly = true)
    public Chat getById(Long id) {
        Chat chat = chatRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid chat Id:" + id));
        return chat;
    }

    @Transactional(readOnly = true)
    public List<Chat> getAll() {
        return chatRepository.findAll();
    }
}
