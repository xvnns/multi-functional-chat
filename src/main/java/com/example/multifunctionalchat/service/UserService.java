package com.example.multifunctionalchat.service;

import com.example.multifunctionalchat.domain.User;

import java.util.List;

public interface UserService {
    User add(User user);
    void delete(User user);
    User getById(Long id);
    List<User> getAll();
}
