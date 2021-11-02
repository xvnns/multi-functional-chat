package com.example.multifunctionalchat.service;

import com.example.multifunctionalchat.domain.Chat;
import com.example.multifunctionalchat.domain.User;
import com.example.multifunctionalchat.exception.AddingToTheDatabaseException;
import com.example.multifunctionalchat.exception.DeleteFromDatabaseException;
import com.example.multifunctionalchat.exception.EditRoleException;
import com.example.multifunctionalchat.repository.RoleRepository;
import com.example.multifunctionalchat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import static com.example.multifunctionalchat.domain.RoleName.*;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private ChatService chatService;

    @Transactional
    public void saveUser(User user) throws AddingToTheDatabaseException {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new AddingToTheDatabaseException("Пользователь с таким именем существует в базе данных");
        }
        else {
            user.setRole(roleRepository.findByName(USER));
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userRepository.saveAndFlush(user);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public void deleteUser(User user) throws DeleteFromDatabaseException {
        List<Chat> chatList = chatService.getChatListFromUser(user);
        for (Chat chat : chatList) {
            chat.getUsers().remove(user);
        }
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
    }

    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MODERATOR')")
    @Transactional
    public void blockUser(User user) {
        user.setBlock(true);
        userRepository.saveAndFlush(user);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MODERATOR')")
    @Transactional
    public void unblockUser(User user) {
        user.setBlock(false);
        userRepository.saveAndFlush(user);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @Transactional
    public void renameUser(User user, String newLogin, User registeredUser) {
        if (registeredUser.getRole().getName() == ADMIN ) {
            user.setUsername(newLogin);
            userRepository.saveAndFlush(user);
        }
        else if (registeredUser.getUsername().equals(user.getUsername())) {
            registeredUser.setUsername(newLogin);
            userRepository.saveAndFlush(registeredUser);
        }
        else throw new AccessDeniedException("Невозможно переименовать чат, недостаточно прав");
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(s);
        if (user == null) {
            throw new UsernameNotFoundException("Пользователь " + s + " не найден в базе данных");
        }
        return user;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public void makeModerator(User user) throws EditRoleException {
        if (user.getRole().getName() == USER) {
            user.setRole(roleRepository.findByName(MODERATOR));
            userRepository.saveAndFlush(user);
        }
        else throw new EditRoleException("Пользователь уже является модератором");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public void makeUser(User user) throws EditRoleException {
        if (user.getRole().getName() == MODERATOR) {
            user.setRole(roleRepository.findByName(USER));
            userRepository.saveAndFlush(user);
        }
        else throw new EditRoleException("Ошибка, пользователь не является модератором");
    }

    @Transactional
    public List<User> getUsersByChat(Chat chat) {
        return userRepository.getUserListByChatId(chat.getId());
    }
}
