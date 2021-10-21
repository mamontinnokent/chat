package ru.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.chat.dto.request.ChatCreateRequestDTO;
import ru.chat.dto.request.MessageSendRequestDTO;
import ru.chat.dto.request.UserRegRequestDTO;
import ru.chat.repository.ChatRepository;
import ru.chat.repository.UserInChatRepository;
import ru.chat.repository.UserRepository;
import ru.chat.service.chat_bot.ChatBotService;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;
import ru.chat.service.utils.TestUtils;

import javax.transaction.Transactional;
import java.io.IOException;

@Slf4j
@Transactional
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ChatBotServiceTest {

    @Autowired
    private ChatBotService chatBotService;
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


    @BeforeEach
    void setUp() throws Exception, YouDontHavePermissionExceptiom {
        var userDto1 = new UserRegRequestDTO("test1@gmail.com", "test1", "test");
        var principal1 = testUtils.getPrincipal("test1@gmail.com");
        userService.create(userDto1);
        var chatDto1 = new ChatCreateRequestDTO("test_chat_1", false);
        chatService.create(chatDto1, principal1);
    }

    @Test
    void help() throws YouDontHavePermissionExceptiom, IOException {
        var principal1 = testUtils.getPrincipal("test1@gmail.com");
        var response = chatBotService
                .parser(new MessageSendRequestDTO(1L, 1L, "//help"), principal1);

        var content = response.getBody().get(0).getContent();
        log.info(content);
        Assertions.assertTrue(!content.isBlank() && !content.isEmpty());
    }

    //   * 1. //yBot find {название канала}||{название видео} - в ответ бот присылает ссылку на ролик
    //   *           -v - выводит количество текущих просмотров.
    //   *           -l - выводит количество лайков под видео.
    //   * 2. //yBot help - список доступных команд для взаимодействия.
    //   * 3. //yBot channelInfo {имя канала}. - Первым сообщением от бота выводится имя канала, вторым - ссылки на последние 5 роликов
    //   * 4. //yBot videoCommentRandom {имя канала}||{Название ролика} - Среди комментариев к ролику рандомно выбирается 1 -
    //   *           Первым сообщением бот выводит login человека, который оставил этот комментарий - Вторым сообщением бот выводит сам комментарий

    @Test
    void yBotHelp() throws YouDontHavePermissionExceptiom, IOException {
        var principal1 = testUtils.getPrincipal("test1@gmail.com");
        var response = chatBotService
                .parser(new MessageSendRequestDTO(1L, 1L, "//yBot help"), principal1);

        var content = response.getBody().get(0).getContent();
        log.info(content);
        Assertions.assertTrue(!content.isBlank() && !content.isEmpty());
    }

    @Test
    void yBotFindWithoutFlags() throws YouDontHavePermissionExceptiom, IOException {
        var principal1 = testUtils.getPrincipal("test1@gmail.com");
        var response = chatBotService
                .parser(new MessageSendRequestDTO(1L, 1L, "//yBot find FKJ||FKJ - Just Piano (In partnership with Calm)"), principal1);

        var content = response.getBody().get(0).getContent();
        log.info(content);
        Assertions.assertEquals("https://www.youtube.com/watch?v=AmmFD2OIs_k", content);
    }

    @Test
    void yBotFindWithFlagV() throws YouDontHavePermissionExceptiom, IOException {
        var principal1 = testUtils.getPrincipal("test1@gmail.com");
        var response = chatBotService
                .parser(new MessageSendRequestDTO(1L, 1L, "//yBot find FKJ||FKJ - Just Piano (In partnership with Calm) -v"), principal1);

        var content = response.getBody().get(0).getContent();

        log.info(content);
        Assertions.assertTrue(!content.isBlank() && !content.isEmpty());
    }

    @Test
    void yBotFindWithFlagL() throws YouDontHavePermissionExceptiom, IOException {
        var principal1 = testUtils.getPrincipal("test1@gmail.com");
        var response = chatBotService
                .parser(new MessageSendRequestDTO(1L, 1L, "//yBot find FKJ||FKJ - Just Piano (In partnership with Calm) -l"), principal1);

        var content = response.getBody().get(0).getContent();

        log.info(content);
        Assertions.assertTrue(!content.isBlank() && !content.isEmpty());
    }

    @Test
    void yBotFindWithFlagLAndV() throws YouDontHavePermissionExceptiom, IOException {
        var principal1 = testUtils.getPrincipal("test1@gmail.com");
        var response = chatBotService
                .parser(new MessageSendRequestDTO(1L, 1L, "//yBot find FKJ||FKJ - Just Piano (In partnership with Calm) -l -v"), principal1);

        var content = response.getBody().get(0).getContent();

        log.info(content);
        Assertions.assertTrue(!content.isBlank() && !content.isEmpty());
    }


}