package ru.chat.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.stream.Streams;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.chat.dto.chatDTO.ChatCreateDTO;
import ru.chat.dto.chatDTO.ChatResponseDTO;
import ru.chat.entity.Chat;
import ru.chat.entity.User;
import ru.chat.entity.UserInChat;
import ru.chat.entity.enums.ChatRole;
import ru.chat.mapper.ChatMapper;
import ru.chat.repository.ChatRepository;
import ru.chat.repository.UserInChatRepository;
import ru.chat.repository.UserRepository;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.*;
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

    public void delete(Long id) throws YouDontHavePermissionExceptiom {
        UserInChat permission = userInChatRepository.getById(id);
        if (permission.getRole() == ChatRole.ROLE_ADMIN || permission.getRole() == ChatRole.ROLE_CREATOR) {
            chatRepository.delete(permission.getChat());
        } else {
            throw new YouDontHavePermissionExceptiom("You don't have permission");
        }
    }

    public void exit(Long userInChatId) {
        UserInChat user = userInChatRepository.getById(userInChatId);
        userInChatRepository.delete(user);
    }

    public ChatResponseDTO get(Long id) {
        UserInChat userInChat = userInChatRepository.getById(id);
        ChatResponseDTO responseDTO = chatMapper.getFromChat(userInChat.getChat());
        return responseDTO;
    }

    private User fromPrincipal(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(""));
    }

//    public void update(ChatCreateDTO chatCreateDTO) {
//
//    }
}
