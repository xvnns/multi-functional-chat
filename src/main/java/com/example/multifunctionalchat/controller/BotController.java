package com.example.multifunctionalchat.controller;

import com.example.multifunctionalchat.domain.*;
import com.example.multifunctionalchat.service.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BotController {

    @Autowired
    private BotService botService;

    @GetMapping("/chat-bot")
    public String chatBot(Model model) {
        /*try {
            Chat chat = chatService.getChatByName("yBot");
            botService.reloadUsers();
            model.addAttribute("messages", chat.getMessages());
            model.addAttribute("message", new Message());
            model.addAttribute("chat_id", chat.getId());
        } catch (ChatNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        }*/
        return "chatBotRoom";
    }

    @PostMapping("/chat-bot")
    public String readStr(Message message, Authentication authentication, Model model) {
        botService.getCommand(message.getContent(), authentication);
        return "redirect:/chat-bot";
    }
}
