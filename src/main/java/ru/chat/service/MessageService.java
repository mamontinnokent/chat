package ru.chat.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.chat.dto.request.MessageSendRequestDTO;
import ru.chat.entity.Message;
import ru.chat.entity.User;
import ru.chat.entity.enums.ChatRole;
import ru.chat.repository.ChatRepository;
import ru.chat.repository.MessageRepository;
import ru.chat.repository.UserInChatRepository;
import ru.chat.repository.UserRepository;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;

import javax.transaction.Transactional;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class MessageService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserInChatRepository userInChatRepository;

    // * Получаем текущего пользователя, утилитарный метод
    private User fromPrincipal(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // * Отправка сообщений. Отправлять может только не заблокированный пользователь.
    public void send(MessageSendRequestDTO dto) throws YouDontHavePermissionExceptiom {
        var user = this.userInChatRepository.getById(dto.getUserId());
        var currentTime = Timestamp.valueOf(LocalDateTime.now());

        // * Проверка заблокирован ли пользователь
        if (user.getBlockedTime().before(currentTime)) {
            var chat = chatRepository.getById(dto.getChatId());
            var message = messageRepository.save(new Message(
                    user.getUser().getUsername(),
                    dto.getContent(),
                    chat,
                    user
            ));

            user.getMessages().add(message);
            this.userInChatRepository.save(user);

            chat.getMessages().add(message);
            this.chatRepository.save(chat);

            log.info("{} отправил сообщение", user.getUser().getUsername());
        } else {
            throw new YouDontHavePermissionExceptiom("You are banned");
        }
    }

    // * Удаление сообщений. Удалять могут только модератор и админы.
    public void delete(Long userId, Long messageId) throws YouDontHavePermissionExceptiom {
        var user = this.userInChatRepository.getById(userId);

        if (user.getRole() == ChatRole.ROLE_MODERATOR || user.getRole() == ChatRole.ROLE_ADMIN) {
            var message = this.messageRepository.getById(messageId);
            this.messageRepository.delete(message);
            log.info("Сообщение с id - {} было удалено", message.getId());
        } else {
            throw new YouDontHavePermissionExceptiom("You don't have permission");
        }
    }

    public MessageSendRequestDTO save(MessageSendRequestDTO dto) throws YouDontHavePermissionExceptiom {
        var user = this.userInChatRepository.getById(dto.getUserId());
        var currentTime = Timestamp.valueOf(LocalDateTime.now());

        // * Проверка заблокирован ли пользователь
        if (user.getBlockedTime().before(currentTime)) {
            var chat = chatRepository.getById(dto.getChatId());
            var message = new Message(
                    user.getUser().getUsername(),
                    dto.getContent(),
                    chat,
                    user
            );

            user.getMessages().add(message);
            this.messageRepository.save(message);
            this.userInChatRepository.save(user);
            log.info("{} отправил сообщение", user.getUser().getUsername());
            return dto;
        } else {
            throw new YouDontHavePermissionExceptiom("You are banned");
        }
    }
}
