package com.example.multifunctionalchat.controller;
import com.example.multifunctionalchat.domain.Chat;
import com.example.multifunctionalchat.domain.ChatMessage;
import com.example.multifunctionalchat.domain.Message;
import com.example.multifunctionalchat.domain.User;
import com.example.multifunctionalchat.exception.AddingToTheDatabaseException;
import com.example.multifunctionalchat.exception.ChatNotFoundException;
import com.example.multifunctionalchat.exception.DeleteFromDatabaseException;
import com.example.multifunctionalchat.service.BotService;
import com.example.multifunctionalchat.service.ChatService;
import com.example.multifunctionalchat.service.MessageService;
import com.example.multifunctionalchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private BotService botService;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        try {
            messageService.sendMessage(chatMessage);
        } catch (ChatNotFoundException e) {
            return new ChatMessage();
        }
        return chatMessage;
    }

    @PostMapping("{chatName}/message/send")
    public String send(@PathVariable("chatName") String chatName, Message message,
                       @AuthenticationPrincipal User currentUser, Model model) {
        try {
            Chat chat = chatService.getChatByName(chatName);
            message.setChat(chat);
            message.setDate(new Date());
            message.setUser(currentUser);
            messageService.save(message);
            if (chatName.equals("yBot")) {
                botService.getCommand(message.getContent(), currentUser);
            }
        } catch (ChatNotFoundException | AddingToTheDatabaseException | DeleteFromDatabaseException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/chat/get/yBot";
    }
}
