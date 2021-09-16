package com.example.multifunctionalchat.controller;

import com.example.multifunctionalchat.domain.Role;
import com.example.multifunctionalchat.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/role")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public String getRoleList(Model model) {
        model.addAttribute("roles", roleService.getAll());
        return null;
    }

    @GetMapping("/get/{id}")
    public String getRole(@PathVariable Long id, Model model) {
        Role role = roleService.getById(id);
        model.addAttribute("role", role);
        return null;
    }

    @PostMapping("/add-role")
    public String addRole(@Valid Role role, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return null;
        }
        roleService.add(role);
        return null;
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Role role = roleService.getById(id);
        model.addAttribute("role", role);
        return null;
    }

    @PostMapping("/update/{id}")
    public String updateRole(@PathVariable("id") long id, @Valid Role role, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return null;
        }
        roleService.add(role);
        return null;
    }

    @GetMapping("/delete/{id}")
    public String deleteRole(@PathVariable("id") long id) {
        Role role = roleService.getById(id);
        roleService.delete(role);
        return null;
    }
}
