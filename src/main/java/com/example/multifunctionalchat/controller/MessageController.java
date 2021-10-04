package com.example.multifunctionalchat.controller;

import com.example.multifunctionalchat.domain.Message;
import com.example.multifunctionalchat.domain.User;
import com.example.multifunctionalchat.exception.DeleteFromDatabaseException;
import com.example.multifunctionalchat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/message")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/add-message/{chat_id}")
    public String addMessage(@ModelAttribute("message") Message message, @PathVariable("chat_id") Long chatId,
                             BindingResult bindingResult, Authentication authentication, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Ошибка, сообщение не может быть отправлено");
            return "room";
        }
        User user = (User) authentication.getPrincipal();
        if (!user.isBlock()) {
            messageService.sendMessage(chatId, user, message.getContent());
        }
        else {
            model.addAttribute("error", "Ошибка, невозможно отправить сообщение");
        }
        return "redirect:/loadChat/" + chatId;
    }

    @Secured({"ADMIN", "MODERATOR"})
    @GetMapping("/delete/{id}")
    public String deleteMessage(@PathVariable("id") long id, @RequestParam Long chatId,
                                Model model) {
        Message message = messageService.getById(id);
        try {
            messageService.delete(message);
        } catch (DeleteFromDatabaseException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/chat?chatId=" + chatId;
    }
}
