package com.example.multifunctionalchat.controller;

import com.example.multifunctionalchat.domain.Message;
import com.example.multifunctionalchat.exception.DeleteFromDatabaseException;
import com.example.multifunctionalchat.service.ChatService;
import com.example.multifunctionalchat.service.MessageService;
import com.example.multifunctionalchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
@RequestMapping("/message")
public class MessageController {

    private final MessageService messageService;
    private final ChatService chatService;
    private final UserService userService;

    @Autowired
    public MessageController(MessageService messageService, ChatService chatService, UserService userService) {
        this.messageService = messageService;
        this.chatService = chatService;
        this.userService = userService;
    }

    @PostMapping("/add-message/{user_id}/{chat_id}")
    public String addMessage(@ModelAttribute("message") Message message, @PathVariable("user_id") Long userId,
                             @PathVariable("chat_id") Long chatId, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Ошибка, недопустимы данные");
            return "room";
        }
        if (!userService.getById(userId).isBlock()) {
            message.setContent(message.getContent());
            message.setChat(chatService.getById(chatId));
            message.setUser(userService.getById(userId));
            message.setDate(new Date());
            messageService.save(message);
        }
        else {
            model.addAttribute("error", "Ошибка, невозможно отправить сообщение");
        }
        return "redirect:/chat?chatId=" + chatId + "&userId=" + userId;
    }

    @GetMapping("/delete/{id}")
    public String deleteMessage(@PathVariable("id") long id, @RequestParam Long userId, @RequestParam Long chatId,
                                Model model) {
        Message message = messageService.getById(id);
        if (userService.isModerator(userId) || userService.isAdmin(userId)) {
            try {
                messageService.delete(message);
            } catch (DeleteFromDatabaseException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        return "redirect:/chat?chatId=" + chatId + "&userId=" + userId;
    }
}
