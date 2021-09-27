package com.example.multifunctionalchat.domain;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "login")
    @NotBlank(message = "Login cannot be null")
    private String login;

    @ManyToOne
    @JoinColumn(name = "role_id")
    @NotNull(message = "Role cannot be null")
    private Role role;

    @Column(name = "block")
    private boolean block;

    @ManyToMany
    @JoinTable(name = "chat_users",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "chat_id", referencedColumnName = "id")
    )
    private Set<Chat> chats;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private Set<Message> messages;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "creator")
    private Set<Chat> createdChats;

    public User() {
        block = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    public Set<Chat> getChats() {
        return chats;
    }

    public void setChats(Set<Chat> chats) {
        this.chats = chats;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    public Set<Chat> getCreatedChats() {
        return createdChats;
    }

    public void setCreatedChats(Set<Chat> createdChats) {
        this.createdChats = createdChats;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", role=" + role +
                ", block=" + block +
                '}';
    }
}