package com.example.multifunctionalchat.service;

import com.example.multifunctionalchat.domain.Chat;
import com.example.multifunctionalchat.domain.User;
import com.example.multifunctionalchat.exception.AddingToTheDatabaseException;
import com.example.multifunctionalchat.exception.ChatNotFoundException;
import com.example.multifunctionalchat.exception.DeleteFromDatabaseException;
import com.example.multifunctionalchat.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.multifunctionalchat.domain.RoleName.USER;

@Service
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public void save(Chat chat) throws AddingToTheDatabaseException {
        if (!chatRepository.existsChatByName(chat.getName())) {
            chatRepository.saveAndFlush(chat);
        }
        else {
            throw new AddingToTheDatabaseException("Чат с таким названием уже существует");
        }
    }

    @Transactional
    public void removeRoom(String chatName) throws ChatNotFoundException {
        Chat chat = getChatByName(chatName);
        chatRepository.delete(chat);
    }

    @Transactional(readOnly = true)
    public Chat getChatById(Long id) throws IllegalArgumentException{
        return chatRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid chat Id:" + id));
    }

    @Transactional(readOnly = true)
    public List<Chat> getAll() {
        return chatRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Chat getChatByName(String name) throws ChatNotFoundException {
        Chat chat = chatRepository.findByName(name);
        if (chat == null) {
            throw new ChatNotFoundException("Чат не найден");
        }
        return chat;
    }

    @Transactional
    public void addUserById(Chat chat, Long userId, User registeredUser) throws AddingToTheDatabaseException {
        List<User> newUsers = chat.getUsers();
        User user = userService.getUserById(userId);
        if (registeredUser.getRole().getName() == USER && registeredUser.getId().equals(userId)) {
            if (!newUsers.contains(user)) {
                newUsers.add(user);
                chat.setUsers(newUsers);
                chatRepository.saveAndFlush(chat);
            }
            else {
                throw new AddingToTheDatabaseException("Данный пользователь уже находится в чате");
            }
        }
        else throw new AddingToTheDatabaseException("Невозможно добавить пользователя в БД");
    }

    @Transactional
    public void addUserByLogin(String chatName, String username) throws AddingToTheDatabaseException, ChatNotFoundException {
        Chat chat = getChatByName(chatName);
        List<User> newUsers = chat.getUsers();

        User user = (User) userService.loadUserByUsername(username);
        if (!newUsers.contains(user)) {
            newUsers.add(user);
            chat.setUsers(newUsers);
            chatRepository.saveAndFlush(chat);
        }
        else {
            throw new AddingToTheDatabaseException("Данный пользователь уже находится в чате");
        }
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @Transactional
    public void deleteUser(Chat chat, User user) throws DeleteFromDatabaseException {
        List<User> newUsers = chat.getUsers();
        if (newUsers.contains(user)) {
            newUsers.remove(user);
            chat.setUsers(newUsers);
            chatRepository.saveAndFlush(chat);
        }
        else {
            throw new DeleteFromDatabaseException("Данный пользователь не найден в чате");
        }
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @Transactional
    public void deleteUserByLogin(String chatName, String userName) throws DeleteFromDatabaseException, ChatNotFoundException {
        Chat chat = getChatByName(chatName);
        List<User> newUsers = chat.getUsers();
        User user = (User) userService.loadUserByUsername(userName);
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
    public void createPrivateRoom(String name, User user) throws AddingToTheDatabaseException {
        Chat chat = new Chat();
        chat.setName(name);
        chat.setPrivate(true);
        chat.setCreator(user);
        save(chat);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @Transactional
    public void renameRoom(String roomName, String newName) throws ChatNotFoundException {
        Chat chat = getChatByName(roomName);
        chat.setName(newName);
        chatRepository.saveAndFlush(chat);
    }
}