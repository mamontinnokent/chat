package ru.chat.service.chat_bot;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.chat.dto.request.ChatCreateRequestDTO;
import ru.chat.entity.Chat;
import ru.chat.entity.User;
import ru.chat.entity.enums.AppRole;
import ru.chat.entity.enums.ChatRole;
import ru.chat.mapper.ChatMapper;
import ru.chat.repository.ChatRepository;
import ru.chat.repository.UserInChatRepository;
import ru.chat.repository.UserRepository;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;

import javax.transaction.Transactional;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Slf4j
@Component
@Transactional
@AllArgsConstructor
public class RoomOperate {

    private final ChatMapper chatMapper;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final UserInChatRepository userInChatRepository;

    // * Получаем текущего пользователя, утилитарный метод
    private User fromPrincipal(Principal principal) {
        return this.userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    //   * Создаём чатик и создаём объект UserInChat, чтобы у пользователя были права и статус создателя
    public void create(ChatCreateRequestDTO chatDTO, Principal principal) throws YouDontHavePermissionExceptiom {
        var user = this.fromPrincipal(principal);

        // ? Если юзер не заблокирован во всём приложении, то он создаёт чатик
        if (!user.isBlocked()) {
            var chat = this.chatRepository.save(new Chat(chatDTO.getName(), chatDTO.isPrivacy()));
            var userInChat = this.chatMapper.create(user, chat);

            // ? Если юзер админ в приложении, то админ и в чате
            if (user.getRole() == AppRole.ROLE_ADMIN)
                userInChat.setRole(ChatRole.ROLE_ADMIN);

            this.userInChatRepository.save(userInChat);
            log.info("Чат {} был создан", chat.getNameChat());
        } else {
            // !  иначе пользователь заблокирован и кидается exception
            throw new YouDontHavePermissionExceptiom("You are blocked");
        }
    }

    //   * Находим юзера в чатике, смотрим роль и если всё ок, то удаляем
    public void delete(String chatName, Principal principal) throws YouDontHavePermissionExceptiom {
        var user = this.fromPrincipal(principal);
        var chat = chatRepository.getByNameChat(chatName);
        var permission = userInChatRepository.findByUserAndChat(user, chat);

        if (permission.getRole() == ChatRole.ROLE_ADMIN || permission.getRole() == ChatRole.ROLE_CREATOR) {
            this.chatRepository.delete(permission.getChat());
            log.info("Чат {} был удален", permission.getChat().getNameChat());
        } else {
            // ! иначе нет прав и кидается exception
            throw new YouDontHavePermissionExceptiom("You don't have permission");
        }
    }

    public void add(String chatName, Principal principal) throws YouDontHavePermissionExceptiom {
        var user = this.fromPrincipal(principal);
        var chat = this.chatRepository.getByNameChat(chatName);
        var userInChat = this.userInChatRepository.findByUserAndChat(user, chat);
        var currentDate = Timestamp.valueOf(LocalDateTime.now());

        // * Если юзер уже был в чатике, то просто обновляем статус
        if (userInChat == null) {
            userInChat = this.chatMapper.create(user, chat);

            // * Если юзер админ в приложении, то он и в чатике админ
            if (user.getRole() == AppRole.ROLE_ADMIN)
                userInChat.setRole(ChatRole.ROLE_ADMIN);

            this.userInChatRepository.save(userInChat);
            log.info("{} вошёл в чатик {}", userInChat.getUser().getUsername(), userInChat.getChat().getNameChat());
        } else if (userInChat.getKickedTime().before(currentDate)) {
            userInChat.setInChat(true);
            this.userInChatRepository.save(userInChat);
        } else {
            throw new YouDontHavePermissionExceptiom("You can't go in chat");
        }
    }

    //   * Добавляем другого юзера в чат
    public void addOtherUser(String userName, String chatName, Principal principal) throws YouDontHavePermissionExceptiom {
        var chat = this.chatRepository.findByNameChat(chatName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
        var user = this.userRepository.findByUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
        var checked = this.userInChatRepository.findByUserAndChat(user, chat);
        var checker = this.userInChatRepository
                .findByUserAndChat(this.fromPrincipal(principal), chat);

        if (checked == null) {
            checked = chatMapper.create(user, chat);
            userInChatRepository.save(checked);
        } else if (!checked.isInChat() && checker.getRole() != ChatRole.ROLE_USER) {
            checked.setKickedTime(Timestamp.valueOf(LocalDateTime.now().minusYears(11)));
            checked.setInChat(true);

            userInChatRepository.save(checked);
        } else {
            throw new YouDontHavePermissionExceptiom("You can't added this user in chat.");
        }
    }

    // * Обновление чата. Обновляют только создатель и админы
    public void update(Long id, Principal principal, String newName) throws YouDontHavePermissionExceptiom {
        var chat = chatRepository.getById(id);
        var user = this.userInChatRepository
                .findByUserAndChat(this.fromPrincipal(principal), chat);

        if (user.getRole() == ChatRole.ROLE_CREATOR || user.getRole() == ChatRole.ROLE_ADMIN) {
            chat.setNameChat(newName);
            this.chatRepository.save(chat);
            log.info("Чат {} был обновлён", chat.getNameChat());
        } else {
            throw new YouDontHavePermissionExceptiom("You don't have permission");
        }
    }

    public void disconnect(Long chatId, Principal principal) {
        var user = this.fromPrincipal(principal);
        var chat = this.chatRepository.getById(chatId);

        var userInChat = this.userInChatRepository.findByUserAndChat(user, chat);
        userInChat.setInChat(false);
        this.userInChatRepository.save(userInChat);

        log.info("{} вышел из чата {}", userInChat.getUser().getUsername(), userInChat.getChat().getNameChat());
    }

    public void disconnectOtherUser(String username, String chatName, Principal principal) {
        var chat = this.chatRepository.getByNameChat(chatName);
        var user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        var checker = this.userInChatRepository
                .findByUserAndChat(this.fromPrincipal(principal), chat);
        var kickedUser = this.userInChatRepository.findByUserAndChat(user, chat);

        if (checker.getRole() != ChatRole.ROLE_USER) {
            kickedUser.setKickedTime(Timestamp.valueOf(LocalDateTime.now().plusYears(11) ));
            kickedUser.setInChat(false);
        }
    }

    public void disconnectOtherUserForValueMinutes(String chatName, String username, long minuteCount, Principal principal) {
        var admin = this.fromPrincipal(principal);
        var chat = this.chatRepository.getByNameChat(chatName);
        var adminInChat = this.userInChatRepository.findByUserAndChat(admin, chat);

        if (adminInChat.getRole() != ChatRole.ROLE_USER) {
            var currentDate = Timestamp.valueOf(LocalDateTime.now());
            var user = this.userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User with username not found"));
            var userInChat = this.userInChatRepository.findByUserAndChat(user, chat);

            userInChat.setInChat(false);
            userInChat.setKickedTime(Timestamp.valueOf(LocalDateTime.now().plusMinutes(minuteCount)));
            this.userInChatRepository.save(userInChat);
        }
    }
}
