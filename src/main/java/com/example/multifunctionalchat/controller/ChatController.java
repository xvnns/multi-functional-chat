package com.example.multifunctionalchat.controller;

import com.example.multifunctionalchat.domain.Chat;
import com.example.multifunctionalchat.domain.Message;
import com.example.multifunctionalchat.domain.RoleName;
import com.example.multifunctionalchat.domain.User;
import com.example.multifunctionalchat.exception.AddingToTheDatabaseException;
import com.example.multifunctionalchat.exception.DeleteFromDatabaseException;
import com.example.multifunctionalchat.service.ChatService;
import com.example.multifunctionalchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;

    @Autowired
    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    @GetMapping("/chat-list")
    public String chatList(@RequestParam Long userId, Model model) {
        try {
            User user = userService.getById(userId);
            if (userService.isAdmin(userId)) {
                model.addAttribute("chatList", chatService.getAll());
            }
            else {
                model.addAttribute("chatList", user.getChats());
            }
        }
        catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/sign-in";
        }
        return "chat-list";
    }

    @GetMapping("/get/{chat_id}")
    public String getChat(@PathVariable("chat_id") Long chatId, @RequestParam Long userId, Model model) {
        if (userService.isOwner(userId, chatId) || userService.isAdmin(userId) || userService.isModerator(userId)) {
            model.addAttribute("messages", chatService.getById(chatId).getMessages());
            model.addAttribute("message", new Message());
            model.addAttribute("chat_id", chatId);
            model.addAttribute("user_id", userId);
            return "room";
        }
        else {
            model.addAttribute("error", "Доступ к данному чату закрыт");
            return "chat-list";
        }
    }

    @PostMapping("/add-chat")
    public String addChat(@Valid Chat chat, @RequestParam Long userId, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Ошибка, данные некорректны");
            return "chat-list";
        }
        User user = userService.getById(userId);
        if (!user.isBlock()) {
            try {
                chatService.save(chat);
            } catch (AddingToTheDatabaseException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        else {
            model.addAttribute("error", "Ошибка, невозможно добавить чат");
        }
        return "chat-list";
    }

    @PostMapping("/add-user/{id}")
    public String addUser(@Valid Chat chat, @PathVariable("id") Long addUserId, @RequestParam Long userId,
                          BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Ошибка, данные некорректны");
            return "chat-list";
        }
        User user = userService.getById(userId);
        if (!user.isBlock()) {
            try {
                chatService.addUser(chat, addUserId);
            } catch (AddingToTheDatabaseException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        else {
            model.addAttribute("error", "Ошибка, невозможно добавить пользователя в чат");
        }
        return "chat-list";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, @RequestParam Long userId, Model model) {
        Chat chat = chatService.getById(id);
        model.addAttribute("chat", chat);
        return "update-chat";
    }

    @PostMapping("/rename/{id}")
    public String updateChat(@PathVariable("id") long id, @RequestParam Long userId, BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Ошибка, данные некорректны");
            return "update-chat";
        }
        if (userService.isAdmin(userId) || userService.isOwner(userId, id)) {
            try {
                chatService.update(chatService.getById(id));
            } catch (AddingToTheDatabaseException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        else {
            model.addAttribute("error", "Ошибка, невозможно переименвать чат");
        }
        return "update-chat";
    }

    @GetMapping("/delete/{id}")
    public String deleteChat(@PathVariable("id") long id, @RequestParam Long userId, Model model) {
        if (userService.isAdmin(userId) || userService.isOwner(userId, id)) {
            chatService.delete(chatService.getById(id));
        }
        else {
            model.addAttribute("error", "Невозможно удалить комнату, недостаточно прав");
        }
        return "users";
    }

    @GetMapping("/delete-user/{chat-id}/{user-id}")
    public String deleteUserChat(@PathVariable("chat-id") long chatId, @PathVariable("user-id") long deleteUserId,
                                 @RequestParam Long userId, Model model) {
        if (userService.isAdmin(userId) || userService.isOwner(userId, chatId)) {
            try {
                Chat chat = chatService.getById(chatId);
                chatService.deleteUser(chat, userId);
            } catch (DeleteFromDatabaseException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        else {
            model.addAttribute("error", "Ошибка, невозможно удалить чат");
        }
        return "chat-list";
    }
}
