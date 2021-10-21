package ru.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.chat.dto.request.UserRegRequestDTO;
import ru.chat.dto.request.UserUpdateRequestDTO;
import ru.chat.entity.User;
import ru.chat.entity.UserInChat;
import ru.chat.entity.enums.AppRole;
import ru.chat.mapper.UserMapper;
import ru.chat.repository.UserInChatRepository;
import ru.chat.repository.UserRepository;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;
import ru.chat.service.utils.TestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Mock
    private UserMapper userMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserInChatRepository userInChatRepository;

    @Autowired
    private TestUtils testUtils;

    @BeforeEach
    void setUp() {
        userService = new UserService(userMapper, userRepository, userInChatRepository);
    }

    @Test
    @DisplayName("UserService. Проверка создания юзера.")
    void testCreate() throws Exception {
        final var dto = new UserRegRequestDTO("test@gmail.com", "test", "test");
        final var userForCreate = testUtils.getUserMapper().create(dto);
        final var userForSave = testUtils.getUserMapper().create(dto)
                .setId(1L)
                .setPassword(userForCreate.getPassword());

        Mockito
                .when(userMapper.create(dto))
                .thenReturn(userForCreate);

        Mockito
                .when(userRepository.save(userForCreate))
                .thenReturn(userForSave);

        var created = userService.create(dto);
        Assertions.assertEquals(userForSave, created);
    }

    @Test
    @DisplayName("UserService. Проверка создания админа.")
    void createAdmin() throws Exception {
        final var dto = new UserRegRequestDTO("test@gmail.com", "test", "test");
        final var userForCreate = testUtils.getUserMapper().create(dto);
        final var userForSave = testUtils.getUserMapper().create(dto)
                .setId(1L)
                .setPassword(userForCreate.getPassword());

        Mockito
                .when(userMapper.create(dto))
                .thenReturn(userForCreate);

        Mockito
                .when(userRepository.save(userForCreate))
                .thenReturn(userForSave);

        var created = userService.create(dto);
        Assertions.assertEquals(userForSave, created);
    }

    @Test
    @DisplayName("UserService. Проверка получения по id.")
    void getById() {
        final var dto = new UserRegRequestDTO("test@gmail.com", "test", "test");
        final var userForGet = testUtils.getUser(
                1l,
                AppRole.ROLE_USER,
                "test@gmail.com"
        );
        final var list = new ArrayList<UserInChat>();

        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(userForGet));

        Mockito
                .when(this.userInChatRepository.findAllByUserAndInChat(userForGet, true))
                .thenReturn(list);

        Mockito
                .when(userMapper.toUserResponseDTO(userForGet, list))
                .thenReturn(testUtils.getUserMapper().toUserResponseDTO(userForGet, list));

        var got = userService.getById(1L);


        Assertions.assertEquals(1L, got.getId());
        Assertions.assertEquals("user1", got.getUsername());
        Assertions.assertEquals(new HashMap<String, Long>(), got.getChats());
    }

    @Test
    @DisplayName("UserService. Проверка получения по получение всех юзеров.")
    void getAll() {
        final var mapper = testUtils.getUserMapper();
        final var user = testUtils.getUser(
                1l,
                AppRole.ROLE_USER,
                "test@gmail.com"
        );

        final var listUsers = new ArrayList<User>();
        listUsers.add(user);

        var map = new HashMap<String,Long>();
        map.put(user.getUsername(), user.getId());

        Mockito
                .when(userRepository.findAll())
                .thenReturn(listUsers);

        var got = userService.getAll();

        Assertions.assertEquals(map, got);
    }

    @Test
    @DisplayName("UserService. Проверка удаления себя.")
    void delete() {
        String email = "test@gmail.com";
        var user = testUtils.getUser(
                1l,
                AppRole.ROLE_USER,
                email
        );;
        var principal = testUtils.getPrincipal(email);

        Mockito
                .when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        // ! Как это тестить?
        userService.delete(principal);
    }

    @Test
    @DisplayName("UserService. Проверка удаления другого пользователя обычным пользователем.")
    void testDeleteByUser() {
        var admin = testUtils.getUser(
                1l,
                AppRole.ROLE_USER,
                "test@gmail.com"
        );;
        var emailAdmin = admin.getEmail();
        var principal = testUtils.getPrincipal(emailAdmin);

        Mockito
                .when(userRepository.findByEmail(emailAdmin))
                .thenReturn(Optional.of(admin));

        // ! Как это тестить?
        try {
            userService.delete(3L, principal);
            fail("Должен выброситься exception.");
        } catch (YouDontHavePermissionExceptiom e) {
            log.info("Пользователь не может удалять других пользователей");
        }
    }

    @Test
    @DisplayName("UserService. Проверка удаления другого пользователя админом.")
    void testDeleteByAdmin() {
        var admin = testUtils.getUser(
                2l,
                AppRole.ROLE_ADMIN,
                "admin@gmail.com"
        );
        var emailAdmin = admin.getEmail();
        var principal = testUtils.getPrincipal(emailAdmin);

        Mockito
                .when(userRepository.findByEmail(emailAdmin))
                .thenReturn(Optional.of(admin));

        var user = testUtils.getUser(
                1l,
                AppRole.ROLE_USER,
                "test@gmail.com"
        );

        Mockito
                .when(userRepository.getById(3l))
                .thenReturn(user);

        // ! Как это тестить?
        try {
            userService.delete(3L, principal);
            log.info("Админ удалил пользователя.");
        } catch (YouDontHavePermissionExceptiom e) {
            fail("Не должно выброситься exception.");
        }
    }

    @Test
    @DisplayName("UserService. Проверка обновления пользователя.")
    void update() {
        var testRequest = new UserUpdateRequestDTO("teeest", "teeest@gmail.com");
        var user =  testUtils.getUser(
                1l,
                AppRole.ROLE_USER,
                "test@gmail.com"
        );

        String email = "test1@gmail.com";
        Mockito
                .when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        var principal = testUtils.getPrincipal(email);


        user.setUsername("teeest").setEmail("teeest@gmail.com");
        Mockito
                .when(userRepository.save(user))
                .thenReturn(user);

        var response = userService.update(
                testRequest,
                principal
        );

        assertEquals("teeest@gmail.com", response.getEmail());
        assertEquals("teeest", response.getUsername());
    }

    @Test
    @DisplayName("UserService. Получение текущего пользователя.")
    void getCurrent() {
        var user = testUtils.getUser(
                1l,
                AppRole.ROLE_USER,
                "test@gmail.com"
        );

        var email = user.getEmail();
        var principal = testUtils.getPrincipal(email);

        Mockito
                .when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));
        var response = userService.getCurrent(principal);

//        assertEquals();
//        assertEquals();
    }
}