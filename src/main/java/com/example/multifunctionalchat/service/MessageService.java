package com.example.multifunctionalchat.service;

import com.example.multifunctionalchat.domain.Message;
import com.example.multifunctionalchat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Transactional
    public Message add(Message message) {
        return messageRepository.saveAndFlush(message);
    }

    @Transactional
    public void delete(Message message) {
        messageRepository.delete(message);
    }

    @Transactional(readOnly = true)
    public Message getById(Long id) {
        Message message = messageRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid message Id:" + id));
        return message;
    }

    @Transactional(readOnly = true)
    public List<Message> getAll() {
        return messageRepository.findAll();
    }
}
