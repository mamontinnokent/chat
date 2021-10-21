package ru.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.chat.dto.request.ChatCreateRequestDTO;
import ru.chat.dto.request.MessageSendRequestDTO;
import ru.chat.dto.request.UserRegRequestDTO;
import ru.chat.repository.ChatRepository;
import ru.chat.repository.MessageRepository;
import ru.chat.repository.UserInChatRepository;
import ru.chat.repository.UserRepository;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;
import ru.chat.service.utils.TestUtils;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.annotation.DirtiesContext.ClassMode;


@Slf4j
@Transactional
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class MessageServiceTest {
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
    private MessageService messageService;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private TestUtils testUtils;

    void setUp() throws Exception, YouDontHavePermissionExceptiom {
        var userDto1 = new UserRegRequestDTO("test1@gmail.com", "test1", "test");
        var userDto2 = new UserRegRequestDTO("test2@gmail.com", "test2", "test");
        var adminDto1 = new UserRegRequestDTO("admin1@gmail.com", "admin1", "test");

        var principal1 = testUtils.getPrincipal("test1@gmail.com");
        var principal2 = testUtils.getPrincipal("test2@gmail.com");
        var principal3 = testUtils.getPrincipal("admin1@gmail.com");

        userService.create(userDto1);
        userService.create(userDto2);
        userService.createAdmin(adminDto1);


        var chatDto1 = new ChatCreateRequestDTO("test_chat_1", false);

        chatService.create(chatDto1, principal1);
        chatService.add(principal2, 1L);
        chatService.add(principal3, 1L);

        chatService.block(2L, principal3);
    }

    @AfterEach
    void tearDown() {
        messageRepository.deleteAll();
        userInChatRepository.deleteAll();
        userRepository.deleteAll();
        chatRepository.deleteAll();
    }

    @Test
    @DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
    void sendBySimpleUser() throws YouDontHavePermissionExceptiom, Exception {
        setUp();

        try {
            messageService.send(new MessageSendRequestDTO(1L, 1L, "Some msg"));
            var msg = messageRepository.getById(1L);
            var chat = chatRepository.getById(1L);


            Assertions.assertEquals("Some msg", msg.getContent());
            Assertions.assertEquals(chat, msg.getChat());

        } catch (YouDontHavePermissionExceptiom e) {
            fail("Тут не должно быть исключений.");
        }
    }

    @Test
    @DirtiesContext(classMode = ClassMode.AFTER_CLASS)
    void sendByBlockUser() throws YouDontHavePermissionExceptiom, Exception {
        setUp();

        try {
            messageService.send(new MessageSendRequestDTO(2L, 1L, "Some msg"));


            fail("Тут должно быть исключение.");
        } catch (YouDontHavePermissionExceptiom e) {
            // * Всё кул!!!
        }
    }

    @Test
    @DirtiesContext(classMode = ClassMode.AFTER_CLASS)
    void delete() throws YouDontHavePermissionExceptiom, Exception {
        setUp();

        try {
            messageService.send(new MessageSendRequestDTO(1L, 1L, "Some msg"));
            var countBefore = messageRepository.count();

            messageService.delete(3L, 1L);

            // ! wtf?
            var countAfter = messageRepository.count();

            Assertions.assertEquals(1L, countBefore);
            Assertions.assertEquals(0L, countAfter);

        } catch (YouDontHavePermissionExceptiom e) {
            fail("Тут не должно быть исключений.");
        }
    }
}