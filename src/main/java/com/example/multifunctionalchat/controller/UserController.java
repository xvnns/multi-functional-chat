package com.example.multifunctionalchat.controller;

import com.example.multifunctionalchat.domain.User;
import com.example.multifunctionalchat.exception.AddingToTheDatabaseException;
import com.example.multifunctionalchat.exception.DeleteFromDatabaseException;
import com.example.multifunctionalchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getUserList(@AuthenticationPrincipal User currentUser, Model model) {
        model.addAttribute("currentUser", currentUser);
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

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/make-moderator/{id}")
    public String makeModerator(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        userService.makeModerator(user);
        return "redirect:/users";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/make-user/{id}")
    public String makeUser(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        userService.makeUser(user);
        return "redirect:/users";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/rename/{id}")
    public String renameForm(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "rename";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") long userId, Model model) {
        try {
            userService.deleteUser(userId);
        } catch (DeleteFromDatabaseException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/users";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MODERATOR')")
    @GetMapping("/block/{id}")
    public String blockUser(@PathVariable("id") long id) {
        User user = userService.getUserById(id);
        userService.blockUser(user.getUsername());
        return "redirect:/users";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MODERATOR')")
    @GetMapping("/unblock/{id}")
    public String unblockUser(@PathVariable("id") long id) {
        User user = userService.getUserById(id);
        userService.unblockUser(user.getUsername());
        return "redirect:/users";
    }
}
