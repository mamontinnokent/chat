package ru.chat.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.chat.repository.ChatRepository;
import ru.chat.repository.MessageRepository;
import ru.chat.repository.UserInChatRepository;
import ru.chat.repository.UserRepository;

@Service
@AllArgsConstructor
public class MessageService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserInChatRepository userInChatRepository;

}
