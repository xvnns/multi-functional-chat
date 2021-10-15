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

import java.util.ArrayList;
import java.util.List;

import static com.example.multifunctionalchat.domain.RoleName.ADMIN;
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

    @GetMapping
    public String index(Authentication authentication, Model model) {
        User user = (User) authentication.getPrincipal();
        List<Chat> chats = new ArrayList<>();
        List<Chat> chatList = chatService.getAll();
        if(user.getRole().getName() == ADMIN) {
            model.addAttribute("chatList", chats);
        }
        else {
            for (Chat chat : chatList) {
                if (chat.getUsers().contains(user)) {
                    chats.add(chat);
                }
            }
            model.addAttribute("chatList", chatList);
        }
        return "chatRooms";
    }

    @GetMapping("/get/{name}")
    public String getChatRoom(@PathVariable String name, Authentication authentication, Model model) {
        try {
            Chat chat = chatService.getChatByName(name);
            model.addAttribute("chat", chat);
            model.addAttribute("message", new Message());
        } catch (ChatNotFoundException e) {
            return "redirect:/";
        }
        return "messageForm";
    }

    @GetMapping("/get-chat-users/{id}")
    public String getChatUsers(@PathVariable Long id, Authentication authentication, Model model) {
        User user = (User) authentication.getPrincipal();

        Chat chat = chatService.getChatById(id);
        model.addAttribute("chatId", chat.getId());
        model.addAttribute("users", chat.getUsers());
        model.addAttribute("user", new User());


        return "allChatUsers";
    }
/*
    @GetMapping("/room/{chat_id}")
    public String getChat(@PathVariable("chat_id") Long chatId, Model model) {
        if (chatService.getChatById(chatId).getName().equals("yBot")) {
            return "redirect:/chat-bot";
        }
        model.addAttribute("messages", chatService.getChatById(chatId).getMessages());
        model.addAttribute("message", new Message());
        model.addAttribute("chat_id", chatId);
        return "room";
    }
*/
    @PostMapping("{chatId}/add-user/{userId}")
    public String addUser(@PathVariable("chatId") Long chatId, @PathVariable("userId") Long addUserId,
                          Authentication authentication, Model model) {
        User user = (User) authentication.getPrincipal();
        if (!user.isBlock()) {
            try {
                 chatService.addUserById(chatService.getChatById(chatId), addUserId, user);
            } catch (AddingToTheDatabaseException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        else {
            model.addAttribute("error", "Ошибка, невозможно добавить пользователя в чат");
        }
        return "chat-list";
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



    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, @RequestParam Long userId, Model model) {
        Chat chat = chatService.getChatById(id);
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
        if (user.getRole().getName() == USER && user.getChats().contains(chatService.getChatById(id))) {
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
        if (user.getRole().getName() == USER && user.getChats().contains(chatService.getChatById(id))) {
            try {
                chatService.removeRoom(chatService.getChatById(id).getName());
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
        if (user.getRole().getName() == USER && user.getChats().contains(chatService.getChatById(chatId))) {
            try {
                Chat chat = chatService.getChatById(chatId);
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
