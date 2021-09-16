package com.example.multifunctionalchat.service.impl;

import com.example.multifunctionalchat.domain.Role;
import com.example.multifunctionalchat.repository.RoleRepository;
import com.example.multifunctionalchat.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional
    public Role add(Role role) {
        return roleRepository.saveAndFlush(role);
    }

    @Transactional
    public void delete(Role role) {
        roleRepository.delete(role);
    }

    @Transactional(readOnly = true)
    public Role getById(Long id) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid role Id:" + id));
        return role;
    }

    @Transactional(readOnly = true)
    public List<Role> getAll() {
        return roleRepository.findAll();
    }
}
