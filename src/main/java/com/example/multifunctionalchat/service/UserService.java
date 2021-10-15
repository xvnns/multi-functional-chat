package com.example.multifunctionalchat.service;

import com.example.multifunctionalchat.domain.Role;
import com.example.multifunctionalchat.domain.RoleName;
import com.example.multifunctionalchat.domain.User;
import com.example.multifunctionalchat.exception.AddingToTheDatabaseException;
import com.example.multifunctionalchat.exception.DeleteFromDatabaseException;
import com.example.multifunctionalchat.repository.RoleRepository;
import com.example.multifunctionalchat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.*;
import java.util.Set;

import java.util.List;

import static com.example.multifunctionalchat.domain.RoleName.USER;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public void userValidate(User user) throws ConstraintViolationException {

        Validator validator = Validation.buildDefaultValidatorFactory()
                .getValidator();

            Set<ConstraintViolation<User>> violations = validator.validate(user);

        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<User> constraintViolation : violations) {
                sb.append(constraintViolation.getMessage());
            }
            throw new ConstraintViolationException("Error occurred: " + sb, violations);
        }
    }/**/


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

    @Transactional
    @Secured("ADMIN")
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

    @Transactional
    @Secured({"ADMIN", "MODERATOR"})
    public void blockUser(String username) {
        User user = (User) loadUserByUsername(username);
        user.setBlock(true);
        userRepository.saveAndFlush(user);
    }

    @Transactional
    @Secured({"ADMIN", "MODERATOR"})
    public void unblockUser(String username) {
        User user = (User) loadUserByUsername(username);
        user.setBlock(true);
        userRepository.saveAndFlush(user);
    }

    @Transactional
    @Secured({"ADMIN", "USER"})
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

    @Secured("ADMIN")
    public void updateUserRole(String username, RoleName role) {
        User user = (User) loadUserByUsername(username);
        user.setRole(roleRepository.findByName(role));
        userRepository.saveAndFlush(user);
    }

    public boolean existsUserByUsername(User user) {
        return userRepository.existsByUsername(user.getUsername());
    }
}
