package com.example.multifunctionalchat.service;

import com.example.multifunctionalchat.domain.Message;
import com.example.multifunctionalchat.exception.DeleteFromDatabaseException;

import java.util.List;

public interface MessageService {
    boolean save(Message message) ;
    void delete(Message message) throws DeleteFromDatabaseException;
    Message getById(Long id) throws IllegalArgumentException;
    List<Message> getAll();
}
