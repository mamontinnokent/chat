package ru.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.chat.ChatApplication;
import ru.chat.dto.request.ChatCreateRequestDTO;
import ru.chat.dto.request.UserRegRequestDTO;
import ru.chat.entity.Message;
import ru.chat.entity.UserInChat;
import ru.chat.repository.ChatRepository;
import ru.chat.repository.UserInChatRepository;
import ru.chat.repository.UserRepository;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;
import ru.chat.service.utils.TestUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;

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

    @AfterEach
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

            var listMsg = new ArrayList<Message>();
            var setUsers = new HashSet<UserInChat>();

            Assertions.assertEquals(1l, createdChat.getId());
            Assertions.assertEquals("test_chat_1", createdChat.getNameChat());
            Assertions.assertEquals(false, createdChat.isPrivacy());
            Assertions.assertEquals(listMsg, createdChat.getMessages());
            Assertions.assertEquals(setUsers, createdChat.getMembers());

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
    @Disabled
    void exit() {
    }

    @Test
    @Disabled
    void get() {
    }

    @Test
    @Disabled
    void getAll() {
    }

    @Test
    @Disabled
    void add() {
    }

    @Test
    @Disabled
    void update() {
    }

    @Test
    @Disabled
    void block() {
    }

    @Test
    @Disabled
    void setModerator() {
    }

    @Test
    @Disabled
    void addOtherUser() {
    }

    @Test
    @Disabled
    void deletAll() {
    }
}