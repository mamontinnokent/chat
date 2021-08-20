package ru.chat;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import ru.chat.dto.request.ChatCreateRequestDTO;
import ru.chat.dto.request.UserRegRequestDTO;
import ru.chat.mock.utils.TestUtils;
import ru.chat.service.ChatService;
import ru.chat.service.UserService;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;

import static org.fest.assertions.Fail.fail;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ChatServiceTest {

    public static ChatService chatService;
    public static UserService userService;

    @BeforeAll
    public void initServices() throws Exception, YouDontHavePermissionExceptiom {
        chatService = TestUtils.initChatService();
        userService = TestUtils.initUserService();

        // Создание пользователей
        userService.create(new UserRegRequestDTO("user1@gmail.com", "user1", "user1"));
        userService.create(new UserRegRequestDTO("user2@gmail.com", "user2", "user2"));
        userService.create(new UserRegRequestDTO("user3@gmail.com", "user3", "user3"));
        userService.create(new UserRegRequestDTO("user4@gmail.com", "user4", "user4"));
        userService.create(new UserRegRequestDTO("user5@gmail.com", "user5", "user5"));
        userService.create(new UserRegRequestDTO("user6@gmail.com", "user6", "user6"));

        // Создание админов
        userService.createAdmin(new UserRegRequestDTO("admin1@gmail.com", "admin1", "admin1"));
        userService.createAdmin(new UserRegRequestDTO("admin2@gmail.com", "admin2", "admin2"));

        // Создание чатов
        chatService.create(new ChatCreateRequestDTO("chat1", false), TestUtils.getPrincipal("user1@gmail.com"));
        chatService.create(new ChatCreateRequestDTO("chat2", false), TestUtils.getPrincipal("user2@gmail.com"));
        chatService.create(new ChatCreateRequestDTO("chat3", true), TestUtils.getPrincipal("user3@gmail.com"));

        // Добавляем первого админа в чаты
        chatService.add(TestUtils.getPrincipal("admin1@gmail.com"), 1L);
        chatService.add(TestUtils.getPrincipal("admin1@gmail.com"), 2L);
        chatService.add(TestUtils.getPrincipal("admin1@gmail.com"), 3L);

        // Добавляем второго админа в чаты
        chatService.add(TestUtils.getPrincipal("admin2@gmail.com"), 1L);
        chatService.add(TestUtils.getPrincipal("admin2@gmail.com"), 2L);
        chatService.add(TestUtils.getPrincipal("admin2@gmail.com"), 3L);

        // Добавляем первого пользователя в чаты
        chatService.add(TestUtils.getPrincipal("user1@gmail.com"), 2L);
        chatService.add(TestUtils.getPrincipal("user1@gmail.com"), 3L);

        // Добавляем второго пользователя в чаты
        chatService.add(TestUtils.getPrincipal("user2@gmail.com"), 1L);
        chatService.add(TestUtils.getPrincipal("user2@gmail.com"), 3L);

        // Добавляем третьего пользователя в чаты
        chatService.add(TestUtils.getPrincipal("user3@gmail.com"), 1L);
        chatService.add(TestUtils.getPrincipal("user3@gmail.com"), 2L);

        // Добавляем четвёртого пользователя в чаты
        chatService.add(TestUtils.getPrincipal("user4@gmail.com"), 1L);
        chatService.add(TestUtils.getPrincipal("user4@gmail.com"), 2L);
        chatService.add(TestUtils.getPrincipal("user4@gmail.com"), 3L);

        // Добавляем пятого пользователя в чаты
        chatService.add(TestUtils.getPrincipal("user5@gmail.com"), 1L);
        chatService.add(TestUtils.getPrincipal("user5@gmail.com"), 2L);
        chatService.add(TestUtils.getPrincipal("user5@gmail.com"), 3L);

        // Добавляем шестого пользователя в чаты
        chatService.add(TestUtils.getPrincipal("user6@gmail.com"), 1L);
        chatService.add(TestUtils.getPrincipal("user6@gmail.com"), 2L);
        chatService.add(TestUtils.getPrincipal("user6@gmail.com"), 3L);
    }

    @Test
    @DisplayName("Check create")
    public void testGetById() {
        try {
            var dto = new ChatCreateRequestDTO("testCreateChat", true);
            chatService.create(dto, TestUtils.getPrincipal("user1@gmail.com"));
            var chat = chatService.get(
                    chatService.getChatRepository().count(),
                    TestUtils.getPrincipal("user1@gmail.com")
            );

            Assertions.assertEquals(dto.getName(), chat.getNameChat());

        } catch (YouDontHavePermissionExceptiom e) {
            fail("Exception " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Check getting all chats for the user")
    public void testGetAllForUser() {
        var principal = TestUtils.getPrincipal("user5@gmail.com");
        var listChats = this.chatService.getAllForCurrent(principal);

        Assertions.assertEquals(3, listChats.size());
    }
}
