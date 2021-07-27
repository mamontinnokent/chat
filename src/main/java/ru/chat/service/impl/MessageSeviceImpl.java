package ru.chat.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.chat.entity.Chat;
import ru.chat.entity.Message;
import ru.chat.repository.MessageRepository;
import ru.chat.repository.UserInChatRepository;
import ru.chat.service.MessageService;

import java.util.List;

@Service
@AllArgsConstructor
public class MessageSeviceImpl implements MessageService {
    private final MessageRepository msgRepo;

    @Override
    public Message save(MessageDTO msgDTO) {
        // ! some code
        return msgRepo.save(msg);
    }

    @Override
    public List<Message> findAllByChat(Chat chat) {
        return msgRepo.findAllByChat(chat);
    }


}
