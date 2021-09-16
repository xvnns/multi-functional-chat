package com.example.multifunctionalchat.controller;

import com.example.multifunctionalchat.domain.Message;
import com.example.multifunctionalchat.service.MessageService;
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
@RequestMapping("/message")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public String getMessageList(Model model) {
        model.addAttribute("messages", messageService.getAll());
        return null;
    }

    @GetMapping("/get/{id}")
    public String getMessage(@PathVariable Long id, Model model) {
        Message message = messageService.getById(id);
        model.addAttribute("message", message);
        return null;
    }

    @PostMapping("/add-message")
    public String addMessage(@Valid Message message, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return null;
        }
        messageService.add(message);
        return null;
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Message message = messageService.getById(id);
        model.addAttribute("message", message);
        return null;
    }

    @PostMapping("/update/{id}")
    public String updateMessage(@PathVariable("id") long id, @Valid Message message, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return null;
        }
        messageService.add(message);
        return null;
    }

    @GetMapping("/delete/{id}")
    public String deleteMessage(@PathVariable("id") long id) {
        Message message = messageService.getById(id);
        messageService.delete(message);
        return null;
    }
}
