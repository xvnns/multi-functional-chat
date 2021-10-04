package com.example.multifunctionalchat.controller;

import com.example.multifunctionalchat.domain.User;
import com.example.multifunctionalchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
public class RegistrationController {
    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    public String showForm(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/registration")
    public String getUser(@ModelAttribute("userForm") @Valid User user, BindingResult bindingResult,
                          @RequestParam("confirmedPassword") String confirmedPassword, Model model) {
        if (!user.getPassword().equals(confirmedPassword)){
            model.addAttribute("passwordError", "Пароли не совпадают");
            return "registration";
        }
        try {
            userService.loadUserByUsername(user.getUsername());
        } catch (UsernameNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "registration";
        }
        return "redirect:/";
    }
}