package com.example.multifunctionalchat.service;

import com.example.multifunctionalchat.domain.Chat;
import com.example.multifunctionalchat.domain.ChatMessage;
import com.example.multifunctionalchat.domain.Message;
import com.example.multifunctionalchat.domain.User;
import com.example.multifunctionalchat.exception.ChatNotFoundException;
import com.example.multifunctionalchat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    ChatService chatService;

    @Autowired
    UserService userService;

    @Autowired
    BotService botService;

    @Transactional
    public void sendMessage(ChatMessage chatMessage) throws ChatNotFoundException {
        User creator = (User) userService.loadUserByUsername(chatMessage.getAuthor());
        if (!creator.isBlock()) {
            Message message = new Message();
            message.setChat(chatService.getChatByName(chatMessage.getRoom()));
            message.setUser(creator);
            message.setContent(chatMessage.getText());
            message.setDate(new Date());
            messageRepository.saveAndFlush(message);
        }
        else throw new AccessDeniedException("Невозможно отправить сообщение, недостаточно прав");
    }

    @Transactional
    public void save(Message message) {
        User creator = message.getUser();
        if (!creator.isBlock()) {
            messageRepository.saveAndFlush(message);
        }
        else throw new AccessDeniedException("Невозможно отправить сообщение, недостаточно прав");
    }
}