package ru.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.chat.dto.request.ChatCreateRequestDTO;
import ru.chat.entity.Chat;
import ru.chat.mapper.ChatMapper;
import ru.chat.repository.ChatRepository;
import ru.chat.repository.UserInChatRepository;
import ru.chat.repository.UserRepository;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;
import ru.chat.service.utils.TestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;


@Slf4j
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @InjectMocks
    private ChatService chatService;

    @Mock
    private ChatMapper chatMapper;
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserInChatRepository userInChatRepository;

    @Autowired
    private TestUtils testUtils;

    @BeforeEach
    void setUp() {
        chatService = new ChatService(chatMapper, chatRepository, userRepository, userInChatRepository);
    }

    @Test
    @DisplayName("ChatService. Проверка создания чат не заблокированным пользователем")
    void createBySimpleUser() {
        final var user = testUtils.userList.get(1);
        final var principal = testUtils.getPrincipal(user.getEmail());
        final var dto = new ChatCreateRequestDTO("test_chat", false);
        final var chat = new Chat(dto.getName(), dto.isPrivacy());
        final var chatWithId = new Chat(dto.getName(), dto.isPrivacy()).setId(1l);

        Mockito
                .when(userRepository.findByEmail(principal.getName()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(chatRepository.save(chat))
                .thenReturn(chatWithId);

        var userInChat = testUtils.getChatMapper().create(user, chat);
        var userInChatWithId = testUtils.getChatMapper().create(user, chat).setId(1L);
        Mockito
                .when(chatMapper.create(user, chatWithId))
                .thenReturn(userInChat);

        Mockito
                .when(userInChatRepository.save(userInChat))
                .thenReturn(userInChatWithId);


        try {
            chatService.create(
                    dto,
                    principal
            );
        } catch (YouDontHavePermissionExceptiom e) {
            fail("Тут не должно быть исключения.");
        }
    }

    @Test
    @DisplayName("ChatService. Проверка создания чат заблокированным пользователем")
    void createByBlockedUser() {
        final var user = testUtils.userList.get(1).setBlocked(true);
        final var principal = testUtils.getPrincipal(user.getEmail());
        final var dto = new ChatCreateRequestDTO("test_chat", false);

        Mockito
                .when(userRepository.findByEmail(principal.getName()))
                .thenReturn(Optional.of(user));

        try {
            chatService.create(
                    dto,
                    principal
            );
            fail("Тут должно быть исключения.");
        } catch (YouDontHavePermissionExceptiom e) {
            // * Всё кулл !!!
        }
    }

    @Test
    @DisplayName("ChatService. Проверка создание чата заблокированным пользователем")
    void getAllForCurrent() {
        var user = testUtils.userList.get(1);
        var email = user.getEmail();
        var principal = testUtils.getPrincipal(email);
        Mockito
                .when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        Mockito
                .when(userInChatRepository.findAllByUserAndInChat(user, true))
                .thenReturn(testUtils.getById(user.getId(), true));

        var print = testUtils.getById(user.getId(), true);
        var variable = chatService.getAllForCurrent(principal);

        log.info(print.toString());
    }

    @Test
    void delete() {
    }

    @Test
    void exit() {
    }

    @Test
    void get() {
    }

    @Test
    void getAll() {
    }

    @Test
    void add() {
    }

    @Test
    void update() {
    }

    @Test
    void block() {
    }

    @Test
    void setModerator() {
    }

    @Test
    void addOtherUser() {
    }
}