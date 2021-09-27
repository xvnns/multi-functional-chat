package com.example.multifunctionalchat.service;

import com.example.multifunctionalchat.domain.User;
import com.example.multifunctionalchat.exception.AddingToTheDatabaseException;
import com.example.multifunctionalchat.exception.DeleteFromDatabaseException;
import com.example.multifunctionalchat.exception.UserNotFoundException;

import java.util.List;

public interface UserService {
    void save(User user) throws AddingToTheDatabaseException;
    void delete(User user) throws DeleteFromDatabaseException;
    User getById(Long id) throws IllegalArgumentException;
    List<User> getAll();
    User getByLogin(String login) throws UserNotFoundException;
    void block(User user) throws AddingToTheDatabaseException;
    void unblock(User user) throws AddingToTheDatabaseException;
    boolean isAdmin(Long id);
    boolean isModerator(Long id);
    boolean isOwner(Long id, Long chatId);
    void update(User user);
}
