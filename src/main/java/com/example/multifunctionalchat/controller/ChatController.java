package com.example.multifunctionalchat.controller;

import com.example.multifunctionalchat.domain.Chat;
import com.example.multifunctionalchat.domain.Message;
import com.example.multifunctionalchat.domain.User;
import com.example.multifunctionalchat.exception.AddingToTheDatabaseException;
import com.example.multifunctionalchat.exception.ChatNotFoundException;
import com.example.multifunctionalchat.exception.DeleteFromDatabaseException;
import com.example.multifunctionalchat.repository.MessageRepository;
import com.example.multifunctionalchat.service.ChatService;
import com.example.multifunctionalchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.example.multifunctionalchat.domain.RoleName.*;

@Controller
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping
    public String index(@AuthenticationPrincipal User currentUser, Model model) {
        List<Chat> chatList;
        if (currentUser.getRole().getName() != ADMIN) {
            chatList = currentUser.getChats();
        }
        else {
            chatList = chatService.getAll();
        }
        chatList.removeIf(chat -> chat.getName().equals("yBot"));
        model.addAttribute("chatList", chatList);
        model.addAttribute("user", currentUser);
        return "chatRooms";
    }

    @GetMapping("/get/{name}")
    public String getChatRoom(@PathVariable String name, @AuthenticationPrincipal User currentUser, Model model) {
        try {
            Chat chat = chatService.getChatByName(name);
            model.addAttribute("chat", chat);
            model.addAttribute("messages", messageRepository.findAllByChatOrderById(chat));
            model.addAttribute("message", new Message());
            model.addAttribute("currentUser", currentUser);
        } catch (ChatNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        }
        if (name.equals("yBot")) return "chatBotRoom";
        return "messageForm";
    }

    @GetMapping("/get-chat-users/{id}")
    public String getChatUsers(@PathVariable Long id, @AuthenticationPrincipal User currentUser, Model model) {
        Chat chat = chatService.getChatById(id);
        List<User> users = userService.getAll();
        users.removeIf(user -> user.getUsername().equals("yBot"));
        model.addAttribute("chatUsers", chat.getUsers());
        model.addAttribute("users", users);
        model.addAttribute("user", new User());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("chat", chat);
        List<Chat> chatList = chatService.getChatListFromUser(currentUser);
        model.addAttribute("access", currentUser.getRole().getName() == ADMIN || chatList.contains(chat));
        return "allChatUsers";
    }

    @PostMapping("{chatId}/add-user")
    public String addUser(@PathVariable("chatId") Long chatId, User user,
                          @AuthenticationPrincipal User currentUser, Model model) {
        if (!currentUser.isBlock()) {
            try {
                Chat chat = chatService.getChatById(chatId);
                chatService.addUser(chat, userService.getUserById(user.getId()), currentUser);
            } catch (AddingToTheDatabaseException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        else {
            model.addAttribute("error", "Ошибка, невозможно добавить пользователя в чат");
        }
        return "redirect:/chat/get-chat-users/" + chatId;
    }

    @GetMapping("/create-chat-room")
    public String createChatForm(@AuthenticationPrincipal User currentUser, Model model) {
        model.addAttribute("currentUser", currentUser);
        return "createChatRoom";
    }

    @PostMapping("/create-chat-room")
    public String createChat(@RequestParam(value = "chat-name") String name, @AuthenticationPrincipal User currentUser,
                          Model model) {
        if (!currentUser.isBlock()) {
            try {
                chatService.createRoom(name, currentUser);
            } catch (AddingToTheDatabaseException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        else {
            model.addAttribute("error", "Невозможно создать комнату, недостаточно прав");
        }
        return "redirect:/chat";
    }

    @GetMapping("/rename/{id}")
    public String renameChatForm(@PathVariable("id") long id, @AuthenticationPrincipal User currentUser, Model model) {
        Chat chat = chatService.getChatById(id);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("chat", chat);
        return "renameChat";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @PostMapping("/rename/{id}")
    public String updateChatName(@PathVariable("id") long id, @AuthenticationPrincipal User currentUser,
                                 @RequestParam(value = "chat-name") String name, Model model) {
        List<Chat> chatList = chatService.getChatListFromUser(currentUser);
        if (currentUser.getRole().getName() == ADMIN || chatList.contains(chatService.getChatById(id))) {
            Chat chat = chatService.getChatById(id);
            chatService.renameRoom(chat, name, currentUser);
        }
        else {
            model.addAttribute("error", "Ошибка, невозможно переименвать чат");
        }
        return "redirect:/chat/get-chat-users/" + id;
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/delete/{id}")
    public String deleteChat(@PathVariable("id") long id, @AuthenticationPrincipal User currentUser, Model model) {
        List<Chat> chatList = chatService.getChatListFromUser(currentUser);
        Chat chat = chatService.getChatById(id);
        if (currentUser.getRole().getName() == ADMIN || chatList.contains(chatService.getChatById(id))) {
            try {
                chatService.removeRoom(chat, currentUser);
            } catch (ChatNotFoundException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        else {
            model.addAttribute("error", "Невозможно удалить комнату, недостаточно прав");
        }
        return "redirect:/chat";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @GetMapping("/delete-user/{chat-id}/{user-id}")
    public String deleteUserChat(@PathVariable("chat-id") long chatId, @PathVariable("user-id") long userId,
                                 @AuthenticationPrincipal User currentUser, Model model) {
        Chat chat = chatService.getChatById(chatId);
        List<Chat> chatList = chatService.getChatListFromUser(currentUser);
        if (currentUser.getRole().getName() == ADMIN || chatList.contains(chat)) {
            try {
                User user = userService.getUserById(userId);
                chatService.deleteUser(chat, user, currentUser);
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
