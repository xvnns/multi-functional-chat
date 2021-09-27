package com.example.multifunctionalchat.domain;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "content")
    @NotBlank(message = "Message content cannot be null")
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id_creator")
    @NotNull(message = "User cannot be null")
    private User user;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    @NotNull(message = "Chat cannot be null")
    private Chat chat;

    @Column(name = "date")
    @NotNull(message = "Date cannot be null")
    private Date date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", user=" + user +
                ", chat=" + chat +
                ", date=" + date +
                '}';
    }
}
