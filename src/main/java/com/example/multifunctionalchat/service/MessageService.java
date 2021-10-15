package com.example.multifunctionalchat.service;

import com.example.multifunctionalchat.domain.Chat;
import com.example.multifunctionalchat.domain.ChatMessage;
import com.example.multifunctionalchat.domain.Message;
import com.example.multifunctionalchat.domain.User;
import com.example.multifunctionalchat.exception.ChatNotFoundException;
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
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    ChatService chatService;

    @Autowired
    UserService userService;

    @Transactional
    public boolean sendMessage(ChatMessage chatMessage) throws ChatNotFoundException {
        User creator = (User) userService.loadUserByUsername(chatMessage.getAuthor());
        if (!creator.isBlock()) {
            Message message = new Message();
            message.setChat(chatService.getChatByName(chatMessage.getRoom()));
            message.setUser(creator);
            message.setContent(chatMessage.getText());
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