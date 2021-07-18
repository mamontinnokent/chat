package ru.chat.service;

import ru.chat.entity.Chat;
import ru.chat.entity.Message;
import java.util.List;

public interface MessageService {
    Message save(MessageDTO msgDTO);
    List<Message> findAllByChat(Chat chat);
}
