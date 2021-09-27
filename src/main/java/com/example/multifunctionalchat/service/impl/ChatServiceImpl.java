package com.example.multifunctionalchat.service.impl;

import com.example.multifunctionalchat.domain.Chat;
import com.example.multifunctionalchat.domain.User;
import com.example.multifunctionalchat.exception.AddingToTheDatabaseException;
import com.example.multifunctionalchat.exception.ChatNotFoundException;
import com.example.multifunctionalchat.exception.DeleteFromDatabaseException;
import com.example.multifunctionalchat.repository.ChatRepository;
import com.example.multifunctionalchat.repository.UserRepository;
import com.example.multifunctionalchat.service.ChatService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    @Autowired
    public ChatServiceImpl(ChatRepository chatRepository, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void save(@NotNull Chat chat) throws AddingToTheDatabaseException {
        if (!chatRepository.existsChatByName(chat.getName())) {
            chatRepository.saveAndFlush(chat);
        }
        else {
            throw new AddingToTheDatabaseException("Чат с таким именем уже существует");
        }
    }

    @Transactional
    public void delete(Chat chat) {
        chatRepository.delete(chat);
    }

    @Transactional(readOnly = true)
    public Chat getById(Long id) throws IllegalArgumentException{
        return chatRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid chat Id:" + id));
    }

    @Transactional(readOnly = true)
    public List<Chat> getAll() {
        return chatRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Chat getByName(String name) throws ChatNotFoundException {
        Chat chat = chatRepository.findByName(name);
        if (chat == null) {
            throw new ChatNotFoundException("Чат не найден");
        }
        return chat;
    }

    @Transactional
    public void addUser(Chat chat, Long userId) throws AddingToTheDatabaseException {
        List<User> newUsers = chat.getUsers();
        User user = userRepository.getById(userId);
        if (!newUsers.contains(user)) {
            newUsers.add(user);
            chat.setUsers(newUsers);
            chatRepository.saveAndFlush(chat);
        }
        else {
            throw new AddingToTheDatabaseException("Данный пользователь уже находится в чате");
        }
    }

    @Transactional
    public void deleteUser(@NotNull Chat chat, Long userId) throws DeleteFromDatabaseException {
        List<User> newUsers = chat.getUsers();
        User user = userRepository.getById(userId);
        if (newUsers.contains(user)) {
            newUsers.remove(user);
            chat.setUsers(newUsers);
            chatRepository.saveAndFlush(chat);
        }
        else {
            throw new DeleteFromDatabaseException("Данный пользователь не найден в чате");
        }
    }

    @Transactional
    public void update(Chat chat) throws AddingToTheDatabaseException {
        save(chat);
    }
}