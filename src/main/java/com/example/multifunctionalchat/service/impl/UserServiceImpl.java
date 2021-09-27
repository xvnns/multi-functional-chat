package com.example.multifunctionalchat.service.impl;

import com.example.multifunctionalchat.domain.Chat;
import com.example.multifunctionalchat.domain.RoleName;
import com.example.multifunctionalchat.domain.User;
import com.example.multifunctionalchat.exception.AddingToTheDatabaseException;
import com.example.multifunctionalchat.exception.DeleteFromDatabaseException;
import com.example.multifunctionalchat.exception.UserNotFoundException;
import com.example.multifunctionalchat.repository.ChatRepository;
import com.example.multifunctionalchat.repository.UserRepository;
import com.example.multifunctionalchat.service.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ChatRepository chatRepository) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
    }

    @Transactional
    public void save(User user) throws AddingToTheDatabaseException {
        if (userRepository.existsByLogin(user.getLogin())) {
            throw new AddingToTheDatabaseException("Пользователь с таким именем существует в базе данных");
        }
        else {
            userRepository.saveAndFlush(user);
        }
    }

    @Transactional
    public void delete(User user) throws DeleteFromDatabaseException {
        if (userRepository.existsByLogin(user.getLogin())) {
            userRepository.delete(user);
        }
        else throw new DeleteFromDatabaseException("Пользователь не найден в базе данных");
    }

    @Transactional(readOnly = true)
    public User getById(Long id) throws IllegalArgumentException{
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
    }

    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getByLogin(String login) throws UserNotFoundException {
        User user = userRepository.findByLogin(login);
        if (user == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        return user;
    }

    @Transactional
    public void block(@NotNull User user) throws AddingToTheDatabaseException {
        user.setBlock(true);
        update(user);
    }

    @Transactional
    public void unblock(@NotNull User user) throws AddingToTheDatabaseException {
        user.setBlock(false);
        update(user);
    }

    public boolean isAdmin(Long id) {
        User user = getById(id);
        return user.getRole().getName() == RoleName.ADMIN;
    }

    public boolean isModerator(Long id) {
        User user = getById(id);
        return user.getRole().getName() == RoleName.MODERATOR;
    }

    public boolean isOwner(Long userId, Long chatId) {
        Chat chat = chatRepository.getById(chatId);
        User user = userRepository.getById(userId);
        return user.getChats().contains(chat);
    }

    @Transactional
    public void update(User user) {
        userRepository.saveAndFlush(user);
    }
}
