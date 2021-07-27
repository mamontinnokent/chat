package ru.chat.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.chat.entity.Chat;
import ru.chat.entity.User;
import ru.chat.repository.ChatRepository;
import ru.chat.repository.UserRepository;
import ru.chat.service.ChatService;

import java.security.Principal;

@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;


    // ? chatDTO ещё не создан
    @Override
    public Chat create(ChatDTO chatDTO, Principal principal) {
        Chat chat = new Chat();
        // ! some code
        return chat;
    }

    @Override
    public void delete(Chat chat, Principal principal) {
        // ! some code
    }

}
