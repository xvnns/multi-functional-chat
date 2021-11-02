package com.example.multifunctionalchat.service;

import com.example.multifunctionalchat.domain.Chat;
import com.example.multifunctionalchat.domain.User;
import com.example.multifunctionalchat.exception.AddingToTheDatabaseException;
import com.example.multifunctionalchat.exception.ChatNotFoundException;
import com.example.multifunctionalchat.exception.DeleteFromDatabaseException;
import com.example.multifunctionalchat.repository.ChatRepository;
import com.example.multifunctionalchat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import static com.example.multifunctionalchat.domain.RoleName.ADMIN;

@Service
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public void save(Chat chat) throws AddingToTheDatabaseException {
        if (!chatRepository.existsChatByName(chat.getName())) {
            chatRepository.saveAndFlush(chat);
        }
        else throw new AddingToTheDatabaseException("Чат с таким названием уже существует");
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @Transactional
    public void removeRoom(Chat chat, User currentUser) throws ChatNotFoundException {
        List<Chat> chatList = getChatListFromUser(currentUser);
        if (currentUser.getRole().getName() == ADMIN || chatList.contains(chat)) {
            chatList.remove(chat);
            chatRepository.delete(chat);
        }
        else throw new AccessDeniedException("Невозможно удалить чат, недостаточно прав");
    }

    @Transactional(readOnly = true)
    public Chat getChatById(Long id) throws IllegalArgumentException {
        return chatRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid chat Id:" + id));
    }

    @Transactional(readOnly = true)
    public List<Chat> getAll() {
        return chatRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Chat getChatByName(String name) throws ChatNotFoundException {
        Chat chat = chatRepository.getChatByName(name);
        if (chat == null) {
            throw new ChatNotFoundException("Чат не найден");
        }
        return chat;
    }

    @Transactional
    public void addUser(Chat chat, User user, User registeredUser) throws AddingToTheDatabaseException {
        List<User> newUsers = chat.getUsers();
        if (!registeredUser.isBlock() && !chat.isPrivate()) {
            if (newUsers == null) {
                newUsers = new ArrayList<>();
            }
            if (!newUsers.contains(user) && (newUsers.size() < 2 || !chat.isPrivate())) {
                user.getChats().add(chat);
                userRepository.saveAndFlush(user);
            }
            else throw new AddingToTheDatabaseException("Невозможно добавить пользователя в комнату");
        }
        else throw new AddingToTheDatabaseException("Невозможно добавить пользователя в комнату, недостаточно прав");
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @Transactional
    public void deleteUser(Chat chat, User user, User registeredUser) throws DeleteFromDatabaseException {
        List<Chat> chatList = getChatListFromUser(registeredUser);
        if (!chat.getCreator().equals(user)) {
            if (registeredUser.getRole().getName() == ADMIN || chatList.contains(chat)) {
                List<User> newUsers = chat.getUsers();
                if (newUsers.contains(user)) {
                    user.getChats().remove(chat);
                    userRepository.saveAndFlush(user);
                }
                else throw new DeleteFromDatabaseException("Данный пользователь не найден в чате");
            }
            else throw new AccessDeniedException("Невозможно удалить чат, недостаточно прав");
        }
        else throw new DeleteFromDatabaseException("Нельзя удалить из комнаты создателя комнаты");
    }

    @Transactional(readOnly = true)
    public List<Chat> getChatListFromUser(User user) {
        return chatRepository.getChatListByUserId(user.getId());
    }

    @Transactional
    public void createRoom(String name, User registeredUser) throws AddingToTheDatabaseException {
        if (!registeredUser.isBlock()) {
            Chat chat = new Chat();
            chat.setName(name);
            chat.setPrivate(false);
            chat.setCreator(registeredUser);
            save(chat);
            addUser(chat, registeredUser, registeredUser);
        }
        else throw new AccessDeniedException("Невозможно создать комнату, недостаточно прав");
    }

    @Transactional
    public void createPrivateRoom(String name, User registeredUser) throws AddingToTheDatabaseException {
        if (!registeredUser.isBlock()) {
            Chat chat = new Chat();
            chat.setName(name);
            chat.setPrivate(true);
            chat.setCreator(registeredUser);
            save(chat);
            addUser(chat, registeredUser, registeredUser);
        }
        else throw new AccessDeniedException("Невозможно создать комнату, недостаточно прав");
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @Transactional
    public void renameRoom(Chat chat, String name, User registeredUser) {
        List<Chat> chatList = getChatListFromUser(registeredUser);
        if (registeredUser.getRole().getName() == ADMIN || chatList.contains(chat)) {
            chat.setName(name);
            chatRepository.save(chat);
        }
        else throw new AccessDeniedException("Невозможно создать комнату, недостаточно прав");
    }

}