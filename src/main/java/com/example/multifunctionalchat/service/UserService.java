package com.example.multifunctionalchat.service;

import com.example.multifunctionalchat.domain.Role;
import com.example.multifunctionalchat.domain.User;
import com.example.multifunctionalchat.exception.AddingToTheDatabaseException;
import com.example.multifunctionalchat.exception.DeleteFromDatabaseException;
import com.example.multifunctionalchat.repository.RoleRepository;
import com.example.multifunctionalchat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Transactional
    public void saveUser(User user) throws AddingToTheDatabaseException {
        // userValidate(user);
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new AddingToTheDatabaseException("Пользователь с таким именем существует в базе данных");
        }
        else {
            user.setRole(new Role(1L, USER));
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userRepository.saveAndFlush(user);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public void deleteUser(Long userId) throws DeleteFromDatabaseException {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
        }
        else throw new DeleteFromDatabaseException("Пользователь не найден в базе данных");
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
    public void blockUser(String username) {
        User user = (User) loadUserByUsername(username);
        user.setBlock(true);
        userRepository.saveAndFlush(user);
    }


    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MODERATOR')")
    @Transactional
    public void unblockUser(String username) {
        User user = (User) loadUserByUsername(username);
        user.setBlock(true);
        userRepository.saveAndFlush(user);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    @Transactional
    public void updateLogin(String userLogin, String newLogin, User registeredUser) throws AddingToTheDatabaseException {
        if (registeredUser.getRole().getName() == USER && registeredUser.getUsername().equals(userLogin)) {
            User user = (User) loadUserByUsername(userLogin);
            user.setUsername(newLogin);
            userRepository.saveAndFlush(user);
        }
        else throw new AddingToTheDatabaseException("Невозможно переименовать пользователя");
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
    public void makeModerator(User user) {
        if (user.getRole().getName() == USER) {
            user.setRole(roleRepository.findByName(MODERATOR));
            userRepository.saveAndFlush(user);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public void makeUser(User user) {
        if (user.getRole().getName() == MODERATOR) {
            user.setRole(roleRepository.findByName(USER));
            userRepository.saveAndFlush(user);
        }
    }
}
