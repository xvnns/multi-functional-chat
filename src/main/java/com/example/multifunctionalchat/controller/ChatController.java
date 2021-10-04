package com.example.multifunctionalchat.controller;

import com.example.multifunctionalchat.domain.Chat;
import com.example.multifunctionalchat.domain.Message;
import com.example.multifunctionalchat.domain.User;
import com.example.multifunctionalchat.exception.AddingToTheDatabaseException;
import com.example.multifunctionalchat.exception.ChatNotFoundException;
import com.example.multifunctionalchat.exception.DeleteFromDatabaseException;
import com.example.multifunctionalchat.service.ChatService;
import com.example.multifunctionalchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.example.multifunctionalchat.domain.RoleName.USER;

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
    public String chatList(Authentication authentication, Model model) {
        try {
            model.addAttribute("chatList", chatService.getAll());
        }
        catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/sign-in";
        }
        return "chat-list";
    }

    @GetMapping("/load-chat/{chat_id}")
    public String getChat(@PathVariable("chat_id") Long chatId, Model model) {
        if (chatService.getById(chatId).getName().equals("yBot")) {
            return "redirect:/chat-bot";
        }
        model.addAttribute("messages", chatService.getById(chatId).getMessages());
        model.addAttribute("message", new Message());
        model.addAttribute("chat_id", chatId);
        return "room";
    }

    @PostMapping("/add-chat")
    public String addChat(@Valid Chat chat, @RequestParam Long userId, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Ошибка, данные некорректны");
            return "chat-list";
        }
        User user = userService.getUserById(userId);
        if (!user.isBlock()) {
            try {
                chatService.save(chat);
            } catch (AddingToTheDatabaseException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        else {
            model.addAttribute("error", "Ошибка, невозможно создать чат");
        }
        return "chat-list";
    }

    @PostMapping("/add-user/{id}")
    public String addUser(@Valid Chat chat, @PathVariable("id") Long addUserId, Authentication authentication,
                          BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Ошибка, данные некорректны");
            return "chat-list";
        }
        User user = (User) authentication.getPrincipal();
        if (!user.isBlock()) {
            try {
                chatService.addUserById(chat, addUserId, user);
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

    @Secured({"ADMIN", "USER"})
    @PostMapping("/rename/{id}")
    public String updateChatName(@PathVariable("id") long id, Authentication authentication, BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Ошибка, данные некорректны");
            return "update-chat";
        }
        User user = (User) authentication.getPrincipal();
        if (user.getRole().getName() == USER && user.getChats().contains(chatService.getById(id))) {
            // chatService.renameRoom();
        }
        else {
            model.addAttribute("error", "Ошибка, невозможно переименвать чат");
        }
        return "update-chat";
    }

    @Secured({"ADMIN", "USER"})
    @GetMapping("/delete/{id}")
    public String deleteChat(@PathVariable("id") long id, Authentication authentication, Model model) {
        User user = (User) authentication.getPrincipal();
        if (user.getRole().getName() == USER && user.getChats().contains(chatService.getById(id))) {
            try {
                chatService.removeRoom(chatService.getById(id).getName());
            } catch (ChatNotFoundException e) {
                e.printStackTrace();
            }
        }
        else {
            model.addAttribute("error", "Невозможно удалить комнату, недостаточно прав");
        }
        return "users";
    }

    @Secured({"ADMIN", "USER"})
    @GetMapping("/delete-user/{chat-id}/{user-id}")
    public String deleteUserChat(@PathVariable("chat-id") long chatId, @PathVariable("user-id") long deleteUserId,
                                 Authentication authentication, Model model) {
        User user = (User) authentication.getPrincipal();
        if (user.getRole().getName() == USER && user.getChats().contains(chatService.getById(chatId))) {
            try {
                Chat chat = chatService.getById(chatId);
                chatService.deleteUserById(chat, user.getId());
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
