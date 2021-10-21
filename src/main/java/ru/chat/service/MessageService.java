package ru.chat.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageService {

    UserRepository userRepository;
    ChatRepository chatRepository;
    MessageRepository messageRepository;
    UserInChatRepository userInChatRepository;

    // * Получаем текущего пользователя, утилитарный метод
    private User fromPrincipal(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // * Отправка сообщений. Отправлять может только не заблокированный пользователь.
    public MessageSendRequestDTO send(MessageSendRequestDTO dto) throws YouDontHavePermissionExceptiom {
        var user = this.userInChatRepository.getById(dto.getUserId());
        var currentTime = Timestamp.valueOf(LocalDateTime.now());

        // * Проверка заблокирован ли пользователь
        if (user.getBlockedTime().before(currentTime) && !user.getUser().isBlocked()) {
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

    // * Удаление сообщений. Удалять могут только модератор и админы.
    public void delete(Long userInChatId, Long messageId) throws YouDontHavePermissionExceptiom {
        var user = this.userInChatRepository.getById(userInChatId);

        if (user.getRole() == ChatRole.ROLE_MODERATOR || user.getRole() == ChatRole.ROLE_ADMIN) {
            this.messageRepository.deleteById(messageId);
            log.info("Сообщение с id - {} было удалено", messageId);
        } else {
            throw new YouDontHavePermissionExceptiom("You don't have permission");
        }
    }
}
