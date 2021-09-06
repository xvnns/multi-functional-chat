package com.example.multifunctionalchat.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "chat_users")
public class ChatUsers {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
