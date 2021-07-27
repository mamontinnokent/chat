package ru.chat.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.chat.dto.chatDTO.ChatCreateDTO;
import ru.chat.entity.Chat;
import ru.chat.entity.User;
import ru.chat.entity.UserInChat;
import ru.chat.mapper.ChatMapper;
import ru.chat.repository.ChatRepository;
import ru.chat.repository.UserInChatRepository;
import ru.chat.repository.UserRepository;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
@AllArgsConstructor
public class ChatService {

    private final ChatMapper chatMapper;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final UserInChatRepository userInChatRepository;

    public void create(ChatCreateDTO chatDTO, Principal principal) {
        User user = fromPrincipal(principal);
        Chat chat = chatRepository.save(new Chat(chatDTO.getName(), chatDTO.getCaption()));
        UserInChat userInChat = chatMapper.create(user, chat);
        userInChatRepository.save(userInChat);

        log.info("Chat {} was created", chat);
    }

    public Map<String, Long> getAllForThisUser(Principal principal) {
        User user = fromPrincipal(principal);
        return userInChatRepository.findAllByUser(user).stream()
                .collect(Collectors.toMap(k -> k.getChat().getNameChat(), v -> v.getId()));
    }

    public User fromPrincipal(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(""));
    }

    public ChatResponseDTO get() {
    }
}
