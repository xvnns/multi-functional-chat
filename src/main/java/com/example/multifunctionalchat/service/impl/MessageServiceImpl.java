package com.example.multifunctionalchat.service.impl;

import com.example.multifunctionalchat.domain.Message;
import com.example.multifunctionalchat.exception.DeleteFromDatabaseException;
import com.example.multifunctionalchat.repository.MessageRepository;
import com.example.multifunctionalchat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Transactional
    public boolean save(Message message) {
        messageRepository.saveAndFlush(message);
        return true;
    }

    @Transactional
    public void delete(Message message) throws DeleteFromDatabaseException {
        if (messageRepository.existsById(message.getId())) {
            messageRepository.delete(message);
        }
        else {
            throw new DeleteFromDatabaseException("Сообщения нет в базе данных");
        }
    }

    @Transactional(readOnly = true)
    public Message getById(Long id) throws IllegalArgumentException{
        return messageRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid message Id:" + id));
    }

    @Transactional(readOnly = true)
    public List<Message> getAll() {
        return messageRepository.findAll();
    }
}