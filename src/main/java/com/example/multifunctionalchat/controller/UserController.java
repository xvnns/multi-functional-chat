package com.example.multifunctionalchat.controller;

import com.example.multifunctionalchat.domain.Chat;
import com.example.multifunctionalchat.domain.RoleName;
import com.example.multifunctionalchat.domain.User;
import com.example.multifunctionalchat.exception.AddingToTheDatabaseException;
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

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ChatService chatService;

    @Autowired
    public UserController(UserService userService, ChatService chatService) {
        this.userService = userService;
        this.chatService = chatService;
    }

    @GetMapping
    public String getUserList(Model model) {
        model.addAttribute("users", userService.getAll());
        return "users";
    }

    @GetMapping("/get/{id}")
    public String getUser(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "users";
    }

    @PostMapping("/add-user/{id}")
    public String addUser(@PathVariable("id") Long id, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "users";
        }
        try {
            userService.saveUser(userService.getUserById(id));
        } catch (AddingToTheDatabaseException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "users";
    }

    @GetMapping("/moderator/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "edit-role";
    }

    @Secured("ADMIN")
    @PostMapping("/update-role")
    public String updateRole(@Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Ошибка, данные некорректны");
        }
        //userService.updateUserRole(user);
        model.addAttribute("error", "Невозможно назначить/удалить модератора");
        return "users";
    }

    @GetMapping("/rename/{id}")
    public String renameForm(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "rename";
    }

    @Secured("ADMIN")
    @PostMapping("/update-name")
    public String updateName(@Valid User user,Authentication authentication, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Ошибка, данные некорректны");
            return "users";
        }
        User regUser = (User) authentication.getPrincipal();
        if (user.getUsername().equals(regUser.getUsername())) {
            //userService.updateLogin(user);
        }
        else model.addAttribute("error", "Невозможно переименовать пользователя");
        return "users";
    }

    @Secured("ADMIN")
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") long userId, Model model) {
        try {
            userService.deleteUser(userId);
        } catch (DeleteFromDatabaseException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "users";
    }

    @Secured({"ADMIN", "MODERATOR"})
    @GetMapping("/block/{id}")
    public String blockUser(@PathVariable("id") long id, Authentication authentication, Model model) {
        User user = userService.getUserById(id);
        userService.blockUser(user.getUsername());
        return "users";
    }

    @Secured({"ADMIN", "MODERATOR"})
    @GetMapping("/unblock/{id}")
    public String unblockUser(@PathVariable("id") long id, Model model) {
        User user = userService.getUserById(id);
        userService.unblockUser(user.getUsername());
        return "users";
    }
}
