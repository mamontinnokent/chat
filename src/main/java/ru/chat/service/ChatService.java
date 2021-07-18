package ru.chat.service;

import ru.chat.entity.Chat;

import java.security.Principal;

public interface ChatService {
    Chat create(ChatDTO chatDTO, Principal principal);
    void delete(Chat chat);

}
