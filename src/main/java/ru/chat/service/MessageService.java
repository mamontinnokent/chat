package ru.chat.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.chat.dto.messageDTO.MessageSendDTO;
import ru.chat.entity.Chat;
import ru.chat.entity.Message;
import ru.chat.entity.User;
import ru.chat.entity.UserInChat;
import ru.chat.entity.enums.ChatRole;
import ru.chat.repository.ChatRepository;
import ru.chat.repository.MessageRepository;
import ru.chat.repository.UserInChatRepository;
import ru.chat.repository.UserRepository;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;

import java.security.Principal;

@Slf4j
@Service
@AllArgsConstructor
public class MessageService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserInChatRepository userInChatRepository;

    // * Получаем текущего пользователя, утилитарный метод
    private User fromPrincipal(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(""));
    }

    // * Отправка сообщений. Отправлять может только не заблокированный пользователь.
    public void send(MessageSendDTO dto) throws YouDontHavePermissionExceptiom {
        UserInChat user = this.userInChatRepository.getById(dto.getUserId());

        // * Проверка заблокирован ли пользователь
        if (user.isBlocked() == false) {
            Chat chat = chatRepository.getById(dto.getChatId());
            Message message = new Message(
                    user.getUser().getUsername(),
                    dto.getContent(),
                    chat,
                    user
            );

            user.getMessages().add(message);
            this.messageRepository.save(message);
            this.userInChatRepository.save(user);
            log.info("{} отправил сообщение", user.getUser().getUsername());
        } else {
            throw new YouDontHavePermissionExceptiom("You are banned");
        }
    }

    // * Удаление сообщений. Удалять могут только модератор и админы.
    public void delete(Long userId, Long messageId) throws YouDontHavePermissionExceptiom {
        UserInChat user = this.userInChatRepository.getById(userId);

        if (user.getRole() == ChatRole.ROLE_MODERATOR || user.getRole() == ChatRole.ROLE_ADMIN) {
            Message message = this.messageRepository.getById(messageId);
            this.messageRepository.delete(message);
            log.info("Сообщение с id - {} было удалено", message.getId());
        } else {
            throw new YouDontHavePermissionExceptiom("You don't have permission");
        }
    }
}
