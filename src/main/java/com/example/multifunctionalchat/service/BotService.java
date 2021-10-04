package com.example.multifunctionalchat.service;

import com.example.multifunctionalchat.domain.Chat;
import com.example.multifunctionalchat.domain.User;
import com.example.multifunctionalchat.exception.ChatNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BotService {

    @Autowired
    UserService userService;
    @Autowired
    ChatService chatService;
    @Autowired
    MessageService messageService;

    public void reloadUsers() throws ChatNotFoundException {
        Chat chat = chatService.getChatByName("yBot");
        List<User> chatBotUsers = chat.getUsers();
        for (User user : userService.getAll()) {
            if (!chatBotUsers.contains(user)) {
                chatBotUsers.add(user);
            }
        }
        // messageService.sendMessage(chat.getId(), (User) userService.loadUserByUsername("yBot"), loadHelpMessage());
    }
}