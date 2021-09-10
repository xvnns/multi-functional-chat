package com.example.multifunctionalchat.controller;

import com.example.multifunctionalchat.domain.Role;
import com.example.multifunctionalchat.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/role")
public class RoleController {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public String getRoleList(Model model) {
        model.addAttribute("roles", roleRepository.findAll());
        return "role";
    }

    @GetMapping("/get/{id}")
    public String getRole(@PathVariable Long id, Model model) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid role Id:" + id));
        model.addAttribute("role", role);
        return "role";
    }

    @PostMapping("/add-role")
    public String addRole(Role role) {
        roleRepository.save(role);
        return "redirect:/role";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid role Id:" + id));
        model.addAttribute("role", role);
        return "update-role";
    }

    @PostMapping("/update/{id}")
    public String updateRole(@PathVariable("id") long id, Role role) {
        roleRepository.save(role);
        return "redirect:/role";
    }

    @GetMapping("/delete/{id}")
    public String deleteRole(@PathVariable("id") long id) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid role Id:" + id));
        roleRepository.delete(role);
        return "redirect:/role";
    }
}
