package com.example.multifunctionalchat.service;

import com.example.multifunctionalchat.domain.Message;

import java.util.List;

public interface MessageService {
    Message add(Message message);
    void delete(Message message);
    Message getById(Long id);
    List<Message> getAll();
}
