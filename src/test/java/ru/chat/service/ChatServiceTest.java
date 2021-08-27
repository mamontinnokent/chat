package ru.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.chat.ChatApplication;
import ru.chat.dto.request.ChatCreateRequestDTO;
import ru.chat.dto.request.ChatUpdateRequestDTO;
import ru.chat.dto.request.UserRegRequestDTO;
import ru.chat.entity.Message;
import ru.chat.entity.UserInChat;
import ru.chat.entity.enums.ChatRole;
import ru.chat.repository.ChatRepository;
import ru.chat.repository.UserInChatRepository;
import ru.chat.repository.UserRepository;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;
import ru.chat.service.utils.TestUtils;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;

@Slf4j
@Transactional
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ChatApplication.class)
class ChatServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChatService chatService;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private UserInChatRepository userInChatRepository;
    @Autowired
    private TestUtils testUtils;

    @Before
    void tearDown() {
        userInChatRepository.deleteAll();
        userRepository.deleteAll();
        chatRepository.deleteAll();
    }

    @Test
    @DisplayName("ChatService. Создание чата обычным пользователям.")
    void createBySimpleUser() throws Exception {
        var principal = testUtils.getPrincipal("test@gmail.com");
        userService.create(new UserRegRequestDTO("test@gmail.com", "test1", "test"));

        try {
            var dto = new ChatCreateRequestDTO("test_chat_1", false);

            chatService.create(dto, principal);

            var createdChat = chatRepository.getById(1L);
            var createdUIC = userInChatRepository.getById(1L);
            var createdUser = userRepository.getById(1L);

            var listMsg = new ArrayList<Message>();
            var setUsers = new HashSet<UserInChat>();
            setUsers.add(createdUIC);

            Assertions.assertEquals(1l, createdChat.getId());
            Assertions.assertEquals("test_chat_1", createdChat.getNameChat());
            Assertions.assertEquals(false, createdChat.isPrivacy());
            Assertions.assertEquals(listMsg, createdChat.getMessages());
            Assertions.assertEquals(setUsers, createdChat.getMembers());
            Assertions.assertEquals(setUsers, createdUser.getChats());

            userInChatRepository.deleteAll();
            userRepository.deleteAll();
            chatRepository.deleteAll();
        } catch (YouDontHavePermissionExceptiom e) {
            fail("Не должно быть исключений");
        }
    }


    @Test
    @DisplayName("ChatService. Создание чата заблокированным пользователям.")
    void createByBlockedUser() throws Exception {
        var principal = testUtils.getPrincipal("test@gmail.com");
        var user = userService.create(new UserRegRequestDTO("test@gmail.com", "test1", "test")).setBlocked(true);
        userRepository.save(user);

        try {
            var dto = new ChatCreateRequestDTO("test_chat_1", false);
            chatService.create(dto, principal);
            fail("Должно быть исключение");
        } catch (YouDontHavePermissionExceptiom e) {
            // * Всё кул!!!
        }
    }

    @Test
    @DisplayName("ChatService. Получение всех чатиков для текущего пользователя.")
    void getAllForCurrent() throws Exception {
        var principal = testUtils.getPrincipal("test@gmail.com");
        var userDTO1 = new UserRegRequestDTO("test@gmail.com", "test1", "test");
        userService.create(userDTO1);

        try {
            var dto1 = new ChatCreateRequestDTO("test_chat_1", false);
            var dto2 = new ChatCreateRequestDTO("test_chat_2", false);
            var dto3 = new ChatCreateRequestDTO("test_chat_3", false);
            var dto4 = new ChatCreateRequestDTO("test_chat_4", false);

            chatService.create(dto1, principal);
            chatService.create(dto2, principal);
            chatService.create(dto3, principal);
            chatService.create(dto4, principal);

            var allForCurrent = chatService.getAllForCurrent(principal);

            Assertions.assertEquals(1L, allForCurrent.get("test_chat_1"));
            Assertions.assertEquals(2L, allForCurrent.get("test_chat_2"));
            Assertions.assertEquals(3L, allForCurrent.get("test_chat_3"));
            Assertions.assertEquals(4L, allForCurrent.get("test_chat_4"));

            var uic = userInChatRepository.getById(1L).setInChat(false);
            userInChatRepository.save(uic);

            allForCurrent = chatService.getAllForCurrent(principal);

            Assertions.assertEquals(3, allForCurrent.size());
        } catch (YouDontHavePermissionExceptiom e) {
            fail("Не должно быть исключений.");
        }
    }

    @Test
    @DisplayName("ChatService. Получение всех чатиков для текущего пользователя.")
    void delete() throws Exception {
        var principal = testUtils.getPrincipal("test@gmail.com");
        var userDTO1 = new UserRegRequestDTO("test@gmail.com", "test1", "test");
        userService.create(userDTO1);

        try {
            var dto1 = new ChatCreateRequestDTO("test_chat_1", false);
            var dto2 = new ChatCreateRequestDTO("test_chat_2", false);
            var dto3 = new ChatCreateRequestDTO("test_chat_3", false);
            var dto4 = new ChatCreateRequestDTO("test_chat_4", false);

            chatService.create(dto1, principal);
            chatService.create(dto2, principal);
            chatService.create(dto3, principal);
            chatService.create(dto4, principal);

            var allForCurrent = chatService.getAllForCurrent(principal);

            Assertions.assertEquals(1L, allForCurrent.get("test_chat_1"));
            Assertions.assertEquals(2L, allForCurrent.get("test_chat_2"));
            Assertions.assertEquals(3L, allForCurrent.get("test_chat_3"));
            Assertions.assertEquals(4L, allForCurrent.get("test_chat_4"));

            var uic = userInChatRepository.getById(1L).setInChat(false);
            userInChatRepository.save(uic);

            allForCurrent = chatService.getAllForCurrent(principal);

            Assertions.assertEquals(3, allForCurrent.size());
        } catch (YouDontHavePermissionExceptiom e) {
            fail("Не должно быть исключений.");
        }
    }

    @Test
    @DisplayName("ChatService. Проверка выхода из чатик.")
    void exit() throws Exception {
        var principal = testUtils.getPrincipal("test@gmail.com");
        var userDTO1 = new UserRegRequestDTO("test@gmail.com", "test1", "test");
        userService.create(userDTO1);

        try {
            var dto1 = new ChatCreateRequestDTO("test_chat_1", false);
            chatService.create(dto1, principal);

            chatService.exit(1L);

            var allForCurrent = chatService.getAllForCurrent(principal);
            Assertions.assertEquals(0, allForCurrent.size());
        } catch (YouDontHavePermissionExceptiom e) {
            fail("Не должно быть исключений.");
        }
    }

    @Test
    @DisplayName("ChatService. Получение чатика.")
    void get() throws Exception {
        var principal = testUtils.getPrincipal("test@gmail.com");
        var userDTO1 = new UserRegRequestDTO("test@gmail.com", "test1", "test");
        userService.create(userDTO1);

        try {
            var dto1 = new ChatCreateRequestDTO("test_chat_1", false);
            chatService.create(dto1, principal);

            List<Message> messages = new ArrayList<>();

            var response = chatService.get(1L, principal);
            Assertions.assertEquals("test_chat_1", response.getNameChat());
            Assertions.assertEquals(messages, response.getMessages());

        } catch (YouDontHavePermissionExceptiom e) {
            fail("Не должно быть исключений.");
        }
    }

    @Test
    @DisplayName("ChatService. Получение всех чатиков.")
    void getAll() throws Exception {
        var principal = testUtils.getPrincipal("test@gmail.com");
        var userDTO1 = new UserRegRequestDTO("test@gmail.com", "test1", "test");
        userService.create(userDTO1);

        try {
            var dto1 = new ChatCreateRequestDTO("test_chat_1", false);
            var dto2 = new ChatCreateRequestDTO("test_chat_2", false);
            var dto3 = new ChatCreateRequestDTO("test_chat_3", false);
            var dto4 = new ChatCreateRequestDTO("test_chat_4", false);
            var dto5 = new ChatCreateRequestDTO("test_chat_5", false);
            var dto6 = new ChatCreateRequestDTO("test_chat_6", true);

            chatService.create(dto1, principal);
            chatService.create(dto2, principal);
            chatService.create(dto3, principal);
            chatService.create(dto4, principal);
            chatService.create(dto5, principal);
            chatService.create(dto6, principal);

            var all = chatService.getAll();

            Assertions.assertEquals(1L, all.get("test_chat_1"));
            Assertions.assertEquals(2L, all.get("test_chat_2"));
            Assertions.assertEquals(3L, all.get("test_chat_3"));
            Assertions.assertEquals(4L, all.get("test_chat_4"));
            Assertions.assertEquals(5L, all.get("test_chat_5"));
            Assertions.assertEquals(5, all.size());

        } catch (YouDontHavePermissionExceptiom e) {
            fail("Не должно быть исключений.");
        }
    }

    @Test
    @DisplayName("ChatService. Добавление в чатик.")
    void add() throws Exception {
        var principal1 = testUtils.getPrincipal("test1@gmail.com");
        var principal2 = testUtils.getPrincipal("test2@gmail.com");

        var userDTO1 = new UserRegRequestDTO("test1@gmail.com", "test1", "test");
        var userDTO2 = new UserRegRequestDTO("test2@gmail.com", "test2", "test");

        userService.create(userDTO1);
        userService.create(userDTO2);

        try {
            var dto = new ChatCreateRequestDTO("test_chat_1", false);
            chatService.create(dto, principal1);
            var chat = chatRepository.getById(1L);
            chatService.add(principal2, 1L);
            var newUIC = userInChatRepository.getById(2L);


            Assertions.assertEquals(2L, newUIC.getId());
            Assertions.assertEquals(chat, newUIC.getChat());
            Assertions.assertEquals(chatRepository.getById(1L), newUIC.getChat());
            Assertions.assertEquals(userRepository.getById(2L), newUIC.getUser());

        } catch (YouDontHavePermissionExceptiom e) {
            fail("Не должно быть исключений.");
        }
    }

    @Test
    @DisplayName("ChatService. Обновление чатика.")
    void update() throws Exception {
        var principal = testUtils.getPrincipal("test@gmail.com");
        userService.create(new UserRegRequestDTO("test@gmail.com", "test1", "test"));

        try {
            var dto1 = new ChatCreateRequestDTO("test_chat_1", false);
            chatService.create(dto1, principal);
            chatService.update(new ChatUpdateRequestDTO(1L, "aaaa"), principal);

            var chat = chatRepository.getById(1L);

            Assertions.assertEquals(1L, chat.getId());
            Assertions.assertEquals("aaaa", chat.getNameChat());

        } catch (YouDontHavePermissionExceptiom e) {
            fail("Не должно быть исключений.");
        }
    }

    @Test
    @DisplayName("ChatService. Блокирование пользователя в чате.")
    void block() throws Exception {
        var creator = testUtils.getPrincipal("creator@gmail.com");
        var admin = testUtils.getPrincipal("admin@gmail.com");
        var test = testUtils.getPrincipal("test@gmail.com");

        userService.create(new UserRegRequestDTO("creator@gmail.com", "creator", "test"));
        userService.createAdmin(new UserRegRequestDTO("admin@gmail.com", "admin", "test"));
        userService.create(new UserRegRequestDTO("test@gmail.com", "test", "test"));

        try {
            var dto1 = new ChatCreateRequestDTO("test_chat_1", false);
            chatService.create(dto1, creator);

            chatService.add(admin, 1L);
            chatService.add(test, 1L);

            chatService.block(3L, admin);

            var uic = userInChatRepository.getById(3L);
            Assertions.assertTrue(uic.getBlockedTime().after(Timestamp.valueOf(LocalDateTime.now())));
        } catch (YouDontHavePermissionExceptiom e) {
            fail("Не должно быть исключений.");
        }
    }

    @Test
    @DisplayName("ChatService. Назначение модератора.")
    void setModerator() throws Exception {
        var creator = testUtils.getPrincipal("creator@gmail.com");
        var admin = testUtils.getPrincipal("admin@gmail.com");
        var test = testUtils.getPrincipal("test@gmail.com");

        userService.create(new UserRegRequestDTO("creator@gmail.com", "creator", "test"));
        userService.createAdmin(new UserRegRequestDTO("admin@gmail.com", "admin", "test"));
        userService.create(new UserRegRequestDTO("test@gmail.com", "test", "test"));

        try {
            var dto1 = new ChatCreateRequestDTO("test_chat_1", false);
            chatService.create(dto1, creator);

            chatService.add(admin, 1L);
            chatService.add(test, 1L);

            chatService.setModerator(3L, admin);

            var uic = userInChatRepository.getById(3L);
            Assertions.assertTrue(uic.getRole() == ChatRole.ROLE_MODERATOR);


        } catch (YouDontHavePermissionExceptiom e) {
            fail("Не должно быть исключений.");
        }
    }

    @Test
    @DisplayName("ChatService. Добавление другого пользователя.")
    void addOtherUser() throws Exception {
        var creator = testUtils.getPrincipal("creator@gmail.com");
        var test1 = testUtils.getPrincipal("test1@gmail.com");

        userService.create(new UserRegRequestDTO("creator@gmail.com", "creator", "test"));
        userService.create(new UserRegRequestDTO("test1@gmail.com", "test1", "test"));
        userService.create(new UserRegRequestDTO("test2@gmail.com", "test2", "test"));

        try {
            var dto1 = new ChatCreateRequestDTO("test_chat_1", false);
            chatService.create(dto1, creator);

            chatService.add(test1, 1L);

            chatService.addOtherUser("test2", "test_chat_1", test1);

            var uic = userInChatRepository.getById(3L);

            Assertions.assertEquals(3L, uic.getId());
            Assertions.assertEquals(userRepository.getById(3L), uic.getUser());
            Assertions.assertEquals(chatRepository.getById(1L), uic.getChat());

        } catch (YouDontHavePermissionExceptiom e) {
            fail("Не должно быть исключений.");
        }
    }
}