package com.example.multifunctionalchat.controller;

import com.example.multifunctionalchat.domain.Chat;
import com.example.multifunctionalchat.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private final ChatRepository chatRepository;

    @Autowired
    public ChatController(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @GetMapping
    public String getChatList(Model model) {
        model.addAttribute("chat", chatRepository.findAll());
        return "chat";
    }

    @GetMapping("/get/{id}")
    public String getChat(@PathVariable Long id, Model model) {
        Chat chat = chatRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid chat Id:" + id));
        model.addAttribute("chat", chat);
        return "chat";
    }

    @PostMapping("/add-chat")
    public String addChat(Chat chat) {
        chatRepository.save(chat);
        return "redirect:/chat";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Chat chat = chatRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid chat Id:" + id));
        model.addAttribute("chat", chat);
        return "update-chat";
    }

    @PostMapping("/update/{id}")
    public String updateChat(@PathVariable("id") long id, Chat chat) {
        chatRepository.save(chat);
        return "redirect:/chat";
    }

    @GetMapping("/delete/{id}")
    public String deleteChat(@PathVariable("id") long id) {
        Chat chat = chatRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid chat Id:" + id));
        chatRepository.delete(chat);
        return "redirect:/chat";
    }
}
