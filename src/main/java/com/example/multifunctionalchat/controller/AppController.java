package com.example.multifunctionalchat.controller;

import com.example.multifunctionalchat.domain.Chat;
import com.example.multifunctionalchat.domain.User;
import com.example.multifunctionalchat.service.ChatService;
import com.example.multifunctionalchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class AppController {
    @Autowired
    private UserService userService;

    @Autowired
    private ChatService chatService;

    @GetMapping("/")
    public String index(Authentication authentication, Model model) {
        User user = (User) authentication.getPrincipal();
        List<Chat> chats = new ArrayList<>();
        List<Chat> chatList = chatService.getAll();
        for (Chat chat : chatList) {
            if (chat.getUsers().contains(user)) {
                chats.add(chat);
            }
        }
        model.addAttribute("chatList", chatList);
        return "chatRooms";
    }
}
