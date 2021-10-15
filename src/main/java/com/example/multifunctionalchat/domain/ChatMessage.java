package com.example.multifunctionalchat.domain;

import lombok.Data;

@Data
public class ChatMessage {
    private String text;
    private String author;
    private String room;
}
