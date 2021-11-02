package com.example.multifunctionalchat.controller;

import com.example.multifunctionalchat.domain.User;
import com.example.multifunctionalchat.exception.AddingToTheDatabaseException;
import com.example.multifunctionalchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
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
    public String getUser(@Valid User user, BindingResult bindingResult,
                          @RequestParam("confirmedPassword") String confirmedPassword, HttpServletRequest request,
                          Model model) {

        if (bindingResult.hasErrors()) {
            return "registration";
        }

       if (!user.getPassword().equals(confirmedPassword)){
            model.addAttribute("error", "Пароли не совпадают");
            return "registration";
        }
        try {
            userService.saveUser(user);
        } catch (AddingToTheDatabaseException | ConstraintViolationException e) {
            model.addAttribute("error", e.getMessage());
            return "registration";
        }
        return "redirect:/login";
    }

    @GetMapping(value="/logout")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login";
    }
}