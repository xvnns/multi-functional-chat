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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import static com.example.multifunctionalchat.domain.RoleName.*;

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
    public String index(@AuthenticationPrincipal User currentUser, Model model) {
        List<Chat> chatList = chatService.getAll();
        if(currentUser.getRole().getName() == ADMIN) {
            model.addAttribute("chatList", chatList);
        }
        else {
            model.addAttribute("chatList", currentUser.getChats());
        }
        model.addAttribute("user", currentUser);
        return "chatRooms";
    }

    @GetMapping("/get/{name}")
    public String getChatRoom(@PathVariable String name, Model model) {
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
    public String getChatUsers(@PathVariable Long id, @AuthenticationPrincipal User currentUser, Model model) {
        Chat chat = chatService.getChatById(id);
        model.addAttribute("chatUsers", chat.getUsers());
        model.addAttribute("users", userService.getAll());
        model.addAttribute("user", new User());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("chat", chat);
        return "allChatUsers";
    }

    @PostMapping("/get-chat-users")
    public String getChatUsers(@PathVariable Long id, User user, @AuthenticationPrincipal User currentUser, Model model) {
        //  Chat chat = chatService.getChatById(id);

        return "allChatUsers";
    }

    @PostMapping("{chatId}/add-user/{userId}")
    public String addUser(@PathVariable("chatId") Long chatId, @PathVariable("userId") Long addUserId,
                          @AuthenticationPrincipal User currentUser, Model model) {
        if (!currentUser.isBlock()) {
            try {
                 chatService.addUserById(chatService.getChatById(chatId), addUserId, currentUser);
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

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @PostMapping("/rename/{id}")
    public String updateChatName(@PathVariable("id") long id, @AuthenticationPrincipal User currentUser, BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Ошибка, данные некорректны");
            return "update-chat";
        }
        if (currentUser.getRole().getName() == USER && currentUser.getChats().contains(chatService.getChatById(id))) {
            // chatService.renameRoom();
        }
        else {
            model.addAttribute("error", "Ошибка, невозможно переименвать чат");
        }
        return "update-chat";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/delete/{id}")
    public String deleteChat(@PathVariable("id") long id, @AuthenticationPrincipal User currentUser, Model model) {
        if (currentUser.getRole().getName() == USER && currentUser.getChats().contains(chatService.getChatById(id))) {
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

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/delete-user/{chat-id}/{user-id}")
    public String deleteUserChat(@PathVariable("chat-id") long chatId, @PathVariable("user-id") long userId,
                                 @AuthenticationPrincipal User currentUser, Model model) {
        Chat chat = chatService.getChatById(chatId);
        if (currentUser.getRole().getName() == ADMIN || currentUser.getChats().contains(chat)) {
            try {
                chatService.deleteUser(chat, userService.getUserById(userId));
            } catch (DeleteFromDatabaseException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        else {
            model.addAttribute("error", "Ошибка, невозможно удалить чат");
        }
        return "redirect:/chat/get-chat-users/" + chatId;
    }
}
