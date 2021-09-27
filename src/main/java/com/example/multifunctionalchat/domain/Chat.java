package com.example.multifunctionalchat.domain;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "chat")
public class Chat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    @NotBlank(message = "Name cannot be null")
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id_creator")
    @NotNull(message = "User-creator cannot be null")
    private User creator;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "chats")
    private List<User> users;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chat")
    private List<Message> messages;

    private boolean isPrivate;

    public Chat() {
        isPrivate = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", creator=" + creator +
                ", isPrivate=" + isPrivate +
                '}';
    }
}