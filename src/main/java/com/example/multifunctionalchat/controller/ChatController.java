package com.example.multifunctionalchat.controller;

import com.example.multifunctionalchat.domain.Chat;
import com.example.multifunctionalchat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    public String getChatList(Model model) {
        model.addAttribute("chat", chatService.getAll());
        return null;
    }

    @GetMapping("/get/{id}")
    public String getChat(@PathVariable Long id, Model model) {
        Chat chat = chatService.getById(id);
        model.addAttribute("chat", chat);
        return null;
    }

    @PostMapping("/add-chat")
    public String addChat(@Valid Chat chat, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return null;
        }
        chatService.add(chat);
        return null;
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Chat chat = chatService.getById(id);
        model.addAttribute("chat", chat);
        return null;
    }

    @PostMapping("/update/{id}")
    public String updateChat(@PathVariable("id") long id, @Valid Chat chat, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return null;
        }
        chatService.add(chat);
        return null;
    }

    @GetMapping("/delete/{id}")
    public String deleteChat(@PathVariable("id") long id) {
        Chat chat = chatService.getById(id);
        chatService.delete(chat);
        return null;
    }
}
