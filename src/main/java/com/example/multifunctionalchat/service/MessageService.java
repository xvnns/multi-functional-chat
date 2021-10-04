package com.example.multifunctionalchat.service;

import com.example.multifunctionalchat.domain.Chat;
import com.example.multifunctionalchat.domain.Message;
import com.example.multifunctionalchat.domain.User;
import com.example.multifunctionalchat.exception.DeleteFromDatabaseException;
import com.example.multifunctionalchat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    ChatService chatService;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Transactional
    public boolean sendMessage(Long chatId, User user, String messageContent) {
        if (!user.isBlock()) {
            Chat chat = chatService.getById(chatId);
            Message message = new Message();
            message.setChat(chat);
            message.setUser(user);
            message.setContent(messageContent);
            message.setDate(new Date());
            messageRepository.saveAndFlush(message);
            return true;
        }
        return false;
    }

    @Transactional
    @Secured({"ADMIN", "MODERATOR"})
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
}