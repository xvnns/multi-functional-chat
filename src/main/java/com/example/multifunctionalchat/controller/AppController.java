package com.example.multifunctionalchat.controller;

import com.example.multifunctionalchat.exception.ChatNotFoundException;
import com.example.multifunctionalchat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AppController {
    @Autowired
    private ChatService chatService;

    @GetMapping("/index/{id}")
    public String index(@PathVariable("id") Long userId, Model model) {
        try {
            model.addAttribute("user_id", userId);
            model.addAttribute("chat_id", chatService.getByName("General room").getId());
        } catch (ChatNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "index";
    }
}
