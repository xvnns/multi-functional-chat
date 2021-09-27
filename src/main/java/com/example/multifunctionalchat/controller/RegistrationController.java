package com.example.multifunctionalchat.controller;

import com.example.multifunctionalchat.domain.User;
import com.example.multifunctionalchat.exception.UserNotFoundException;
import com.example.multifunctionalchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {
    @Autowired
    private UserService userService;

    @GetMapping("/sign-in")
    public String showForm(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/sign-in")
    public String getUser(User user, Model model) {
        try {
            user = userService.getByLogin(user.getLogin());
        } catch (UserNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/index/" + user.getId();
    }
}