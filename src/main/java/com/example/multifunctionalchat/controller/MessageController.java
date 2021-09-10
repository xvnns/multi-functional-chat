package com.example.multifunctionalchat.controller;

import com.example.multifunctionalchat.domain.Message;
import com.example.multifunctionalchat.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/message")
public class MessageController {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @GetMapping
    public String getMessageList(Model model) {
        model.addAttribute("messages", messageRepository.findAll());
        return "message";
    }

    @GetMapping("/get/{id}")
    public String getMessage(@PathVariable Long id, Model model) {
        Message message = messageRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid message Id:" + id));
        model.addAttribute("message", message);
        return "message";
    }

    @PostMapping("/add-message")
    public String addMessage(Message message) {
        messageRepository.save(message);
        return "redirect:/message";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Message message = messageRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid message Id:" + id));
        model.addAttribute("message", message);
        return "update-message";
    }

    @PostMapping("/update/{id}")
    public String updateMessage(@PathVariable("id") long id, Message message) {
        messageRepository.save(message);
        return "redirect:/message";
    }

    @GetMapping("/delete/{id}")
    public String deleteMessage(@PathVariable("id") long id, Model model) {
        Message message = messageRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid message Id:" + id));
        messageRepository.delete(message);
        return "redirect:/message";
        }
}
