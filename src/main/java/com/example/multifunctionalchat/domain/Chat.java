package com.example.multifunctionalchat.domain;

import lombok.Data;
import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "chat")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id_creator", nullable = false)
    private User creator;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "chats")
    private List<User> users;
}