package ru.chat.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.chat.dto.chatDTO.ChatCreateDTO;
import ru.chat.dto.chatDTO.ChatResponseDTO;
import ru.chat.dto.chatDTO.ChatUpdateDTO;
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

    // * Получаем текущего пользователя, утилитарный метод
    private User fromPrincipal(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(""));
    }

    // * Создаём чатик и создаём объект UserInChat, чтобы у пользователя были права и статус создателя
    public void create(ChatCreateDTO chatDTO, Principal principal) throws YouDontHavePermissionExceptiom {
        User user = fromPrincipal(principal);

        // ? Если юзер не заблокирован во всём приложении, то он создаёт чатик
        if (!user.isBlocked()) {
            Chat chat = chatRepository.save(new Chat(chatDTO.getName(), chatDTO.getCaption(), chatDTO.isPrivacy()));
            UserInChat userInChat = chatMapper.create(user, chat);
            userInChatRepository.save(userInChat);
            log.info("Chat {} was created", chat);
        } else {
            // !  иначе пользователь заблокирован и кидается exception
            throw new YouDontHavePermissionExceptiom("You are blocked");
        }
    }

    // * Получаем все чатики для текущего юзера
    public Map<String, Long> getAllForThisUser(Principal principal) {
        User user = fromPrincipal(principal);
        return userInChatRepository.findAllByUser(user).stream()
                .collect(Collectors.toMap(k -> k.getChat().getNameChat(), v -> v.getId()));
    }

    // * Находим юзера в чатике, смотрим роль и если всё ок, то удаляем
    public void delete(Long id) throws YouDontHavePermissionExceptiom {
        UserInChat permission = userInChatRepository.getById(id);
        if (permission.getRole() == ChatRole.ROLE_ADMIN || permission.getRole() == ChatRole.ROLE_CREATOR) {
            chatRepository.delete(permission.getChat());
        } else {
            // ! иначе нет прав и кидается exception
            throw new YouDontHavePermissionExceptiom("You don't have permission");
        }
    }

    // * Когда пользователь выходит из чатик он не удаляется из него
    // * иначе если он был заблочен, то он просто перезайдёт и блокировка слетит
    public void exit(Long userInChatId) {
        UserInChat user = userInChatRepository.getById(userInChatId);
        user.setInChat(false);
        userInChatRepository.save(user);
    }

    // * Получаем текущий чатик из списка чатиков юзера
    public ChatResponseDTO get(Long id) {
        UserInChat userInChat = userInChatRepository.getById(id);
        ChatResponseDTO responseDTO = chatMapper.getFromChat(userInChat.getChat());
        return responseDTO;
    }

    // * Получаем ВСЕ чатики
    public Map<String, Long> getAll() {
        return chatRepository.getAllByPrivacy(false).stream()
                .collect(Collectors.toMap(k -> k.getNameChat(), v -> v.getId()));
    }

    // * Добавляем чатик юзеру.
    public void add(Principal principal, Long chatId) {
        User user = this.fromPrincipal(principal);
        Chat chat = chatRepository.getById(chatId);
        UserInChat userInChat = userInChatRepository.findByUserAndChat(user, chat);

        // * Если юзер уже был в чатике, то просто обновляем статус
        if (userInChat == null) {
            userInChat = chatMapper.create(user, chat);

            // * Если юзер админ в приложении, то он и в чатике админ
            if (user.getRole() == AppRole.ROLE_ADMIN)
                userInChat.setRole(ChatRole.ROLE_ADMIN);

            userInChatRepository.save(userInChat);
        } else {
            userInChat.setInChat(true);
            userInChatRepository.save(userInChat);
        }
    }

    public void update(ChatUpdateDTO dto) {
        Chat chat = chatRepository.getById(dto.getId());
        chat.setNameChat(dto.getNameChat());
        chat.setCaption(dto.getCaption());
    }
}
