package com.example.multifunctionalchat.controller;

import com.example.multifunctionalchat.domain.User;
import com.example.multifunctionalchat.exception.AddingToTheDatabaseException;
import com.example.multifunctionalchat.exception.DeleteFromDatabaseException;
import com.example.multifunctionalchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getUserList(Model model) {
        model.addAttribute("users", userService.getAll());
        return "users";
    }

    @GetMapping("/get/{id}")
    public String getUser(@PathVariable Long id, Model model) {
        User user = userService.getById(id);
        model.addAttribute("user", user);
        return "users";
    }

    @PostMapping("/add-user/{id}")
    public String addUser(@PathVariable("id") Long id, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "users";
        }
        try {
            userService.save(userService.getById(id));
        } catch (AddingToTheDatabaseException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "users";
    }

    @GetMapping("/is-moderator/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        User user = userService.getById(id);
        model.addAttribute("user", user);
        return "edit-role";
    }

    @PostMapping("/update-role")
    public String updateRole(@Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Ошибка, данные некорректны");
        }
        if (userService.isAdmin(user.getId())) {
            userService.update(user);
        }
        else model.addAttribute("error", "Невозможно назначить/удалить модератора");
        return "users";
    }

    @GetMapping("/rename/{id}")
    public String renameForm(@PathVariable("id") Long id, Model model) {
        User user = userService.getById(id);
        model.addAttribute("user", user);
        return "rename";
    }

    @PostMapping("/update-name")
    public String updateName(@Valid User user, @RequestParam Long userId, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "Ошибка, данные некорректны");
            return "users";
        }
        if (userService.isAdmin(user.getId()) || user.getLogin().equals(userService.getById(userId).getLogin())) {
            userService.update(user);
        }
        else model.addAttribute("error", "Невозможно переименовать пользователя");
        return "users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") long id, Model model) {
        User user = userService.getById(id);
        try {
            userService.delete(user);
        } catch (DeleteFromDatabaseException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "users";
    }

    @GetMapping("/block/{id}")
    public String blockUser(@PathVariable("id") long id, @RequestParam Long userId, Model model) {
        User user = userService.getById(id);
        if (userService.isModerator(userId) || userService.isAdmin(userId)) {
            try {
                userService.block(user);
            } catch (AddingToTheDatabaseException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        return "users";
    }

    @GetMapping("/unblock/{id}")
    public String unblockUser(@PathVariable("id") long id, @RequestParam Long userId, Model model) {
        User user = userService.getById(id);
        if (userService.isModerator(userId) || userService.isAdmin(userId)) {
            try {
                userService.unblock(user);
            } catch (AddingToTheDatabaseException e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        return "users";
    }
}
