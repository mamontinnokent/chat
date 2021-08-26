package ru.chat.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.chat.dto.request.ChatCreateRequestDTO;
import ru.chat.dto.request.ChatUpdateRequestDTO;
import ru.chat.dto.response.ChatResponseDTO;
import ru.chat.entity.Chat;
import ru.chat.entity.User;
import ru.chat.entity.UserInChat;
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
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class ChatService {

    private final ChatMapper chatMapper;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final UserInChatRepository userInChatRepository;

    // * Получаем текущего пользователя, утилитарный метод
    private User fromPrincipal(Principal principal) {
        return this.userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // * Создаём чатик и создаём объект UserInChat, чтобы у пользователя были права и статус создателя
    public void create(ChatCreateRequestDTO chatDTO, Principal principal) throws YouDontHavePermissionExceptiom {
        var user = this.fromPrincipal(principal);

        // ? Если юзер не заблокирован во всём приложении, то он создаёт чатик
        if (!user.isBlocked()) {
            var chat = this.chatRepository.save(new Chat(chatDTO.getName(), chatDTO.isPrivacy()));
            var userInChat = this.chatMapper.create(user, chat);

            // ? Если юзер админ в приложении, то админ и в чате
            if (user.getRole() == AppRole.ROLE_ADMIN)
                userInChat.setRole(ChatRole.ROLE_ADMIN);

            var save = this.userInChatRepository.save(userInChat);
            log.info("Чат {} был создан", chat.getNameChat());

        } else {
            // !  иначе пользователь заблокирован и кидается exception
            throw new YouDontHavePermissionExceptiom("You are blocked");
        }
    }

    // * Получаем все чатики для текущего юзера
    public Map<String, Long> getAllForCurrent(Principal principal) {
        var user = this.fromPrincipal(principal);
        log.info("Пользователь получил свои чатики", user.getUsername());


        var map = new HashMap<String, Long>();
        var allByUserAndInChat = this.userInChatRepository.findAllByUserAndInChat(user, true);

        allByUserAndInChat.stream()
                .map(uic -> uic.getChat())
                .forEach(chat -> map.put(chat.getNameChat(), chat.getId()));

        return map;
    }


    // * Находим юзера в чатике, смотрим роль и если всё ок, то удаляем
    public void delete(Long id) throws YouDontHavePermissionExceptiom {
        UserInChat permission = this.userInChatRepository.getById(id);

        if (permission.getRole() == ChatRole.ROLE_ADMIN || permission.getRole() == ChatRole.ROLE_CREATOR) {
            this.chatRepository.delete(permission.getChat());
            log.info("Чат {} был удален", permission.getChat().getNameChat());
        } else {
            // ! иначе нет прав и кидается exception
            throw new YouDontHavePermissionExceptiom("You don't have permission");
        }
    }


    // * Когда пользователь выходит из чатик он не удаляется из него
    // * иначе если он был заблочен, то он просто перезайдёт и блокировка слетит
    public void exit(Long userInChatId) {
        var user = this.userInChatRepository.getById(userInChatId);
        user.setInChat(false);
        this.userInChatRepository.save(user);
        log.info("{} вышел из чата {}", user.getUser().getUsername(), user.getChat().getNameChat());
    }

    // * Получаем текущий чатик из списка чатиков юзера
    public ChatResponseDTO get(Long id, Principal principal) {
        var user = this.fromPrincipal(principal);
        var chat = this.chatRepository.getById(id);

        ChatResponseDTO responseDTO = this.chatMapper.getFromChat(chat);

        log.info("Для юзера {} получен чатик {}", user.getUsername(), chat.getNameChat());
        return responseDTO;
    }

    // * Получаем ВСЕ чатики
    public Map<String, Long> getAll() {
        log.info("Получены все чатики");
        return this.chatRepository.getAllByPrivacy(false).stream()
                .collect(Collectors.toMap(k -> k.getNameChat(), v -> v.getId()));
    }

    // * Добавляем чатик юзеру.
    public void add(Principal principal, Long chatId) throws YouDontHavePermissionExceptiom {
        var user = this.fromPrincipal(principal);
        var chat = this.chatRepository.getById(chatId);
        var currentDate = Timestamp.valueOf(LocalDateTime.now());
        var userInChat = userInChatRepository.findByUserAndChat(user, chat);

        // * Если юзер уже был в чатике, то просто обновляем статус
        if (userInChat == null) {
            userInChat = this.chatMapper.addToChat(user, chat);

            // * Если юзер админ в приложении, то он и в чатике админ
            if (user.getRole() == AppRole.ROLE_ADMIN)
                userInChat.setRole(ChatRole.ROLE_ADMIN);

            this.userInChatRepository.save(userInChat);
            log.info("{} вошёл в чатик {}", userInChat.getUser().getUsername(), userInChat.getChat().getNameChat());
        } else {
            if (userInChat.getKickedTime().before(currentDate)) {
                userInChat.setInChat(true);
                this.userInChatRepository.save(userInChat);
            } else {
                throw new YouDontHavePermissionExceptiom("You are kicked");
            }
        }
    }

    // * Обновление чата. Обновляют только создатель и админы
    public void update(ChatUpdateRequestDTO dto, Principal principal) throws YouDontHavePermissionExceptiom {
        var chat = this.chatRepository.getById(dto.getId());
        UserInChat user = this.userInChatRepository
                .findByUserAndChat(this.fromPrincipal(principal), chat);

        if (user.getRole() == ChatRole.ROLE_CREATOR || user.getRole() == ChatRole.ROLE_ADMIN) {
            chat.setNameChat(dto.getNameChat());
            this.chatRepository.save(chat);
            log.info("Чат {} был обновлён", chat.getNameChat());
        } else {
            throw new YouDontHavePermissionExceptiom("You don't have permission");
        }
    }

    // * Блокировка юзера. Блокируют только модераторы и администраторы
    // * Этот же метод отвечает за разблокировку. 
    // * Если пользователь уже заблокирован и он вызовется на него же - он будет разблокирован
    public void block(Long userInChatId, Principal principal) throws YouDontHavePermissionExceptiom {
        var block = this.userInChatRepository.getById(userInChatId);
        var government = this.userInChatRepository
                .findByUserAndChat(this.fromPrincipal(principal), block.getChat());
        var currentTime = Timestamp.valueOf(LocalDateTime.now());

        if ((government.getRole() == ChatRole.ROLE_ADMIN || government.getRole() == ChatRole.ROLE_MODERATOR) && block.getBlockedTime().before(currentTime)) {
            Timestamp blockTime = Timestamp.valueOf(LocalDateTime.now().plusYears(20));
            block.setBlockedTime(blockTime);
            this.userInChatRepository.save(block);
        } else if ((government.getRole() == ChatRole.ROLE_ADMIN || government.getRole() == ChatRole.ROLE_MODERATOR) && block.getBlockedTime().after(currentTime)) {
            Timestamp blockTime = Timestamp.valueOf(LocalDateTime.now().minusYears(203));
            block.setBlockedTime(blockTime);
            this.userInChatRepository.save(block);
        } else {
            throw new YouDontHavePermissionExceptiom("You don't have permission");
        }
    }

    // * Назначение модератора. Назначать могут только админы
    // * Этот же метод отвечает за разблокировку. По аналогии с блокировкой пользователя.
    public void setModerator(Long userInChatId, Principal principal) throws YouDontHavePermissionExceptiom {
        var moderatorInFuture = this.userInChatRepository.getById(userInChatId);
        var admin = this.userInChatRepository
                .findByUserAndChat(this.fromPrincipal(principal), moderatorInFuture.getChat());

        if (admin.getRole() == ChatRole.ROLE_ADMIN && moderatorInFuture.getRole() == ChatRole.ROLE_USER) {
            moderatorInFuture.setRole(ChatRole.ROLE_MODERATOR);
            this.userInChatRepository.save(moderatorInFuture);
        } else if (admin.getRole() == ChatRole.ROLE_ADMIN && moderatorInFuture.getRole() == ChatRole.ROLE_MODERATOR) {
            moderatorInFuture.setRole(ChatRole.ROLE_USER);
            this.userInChatRepository.save(moderatorInFuture);
        } else {
            throw new YouDontHavePermissionExceptiom("You don't have permission");
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
            checked.setKickedTime(Timestamp.valueOf(LocalDateTime.now().minusYears(1)));
            checked.setInChat(true);

            userInChatRepository.save(checked);
        } else {
            throw new YouDontHavePermissionExceptiom("You can't added this user in chat.");
        }
    }
}
