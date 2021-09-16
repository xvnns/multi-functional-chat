package com.example.multifunctionalchat.service;

import com.example.multifunctionalchat.domain.Role;

import java.util.List;

public interface RoleService {
    Role add(Role role);
    void delete(Role role);
    Role getById(Long id);
    List<Role> getAll();
}
