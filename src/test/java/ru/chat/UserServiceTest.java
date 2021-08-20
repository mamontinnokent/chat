package ru.chat;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import ru.chat.dto.request.UserRegRequestDTO;
import ru.chat.dto.request.UserUpdateRequestDTO;
import ru.chat.dto.response.UserResponseDTO;
import ru.chat.entity.User;
import ru.chat.entity.enums.AppRole;
import ru.chat.mock.utils.TestUtils;
import ru.chat.service.ChatService;
import ru.chat.service.UserService;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;

import java.util.HashMap;

import static org.fest.assertions.Fail.fail;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {

    public static UserService userService;
    public static ChatService chatService;

    @BeforeAll
    public void initUserService() {
        this.userService = TestUtils.initUserService();
        this.chatService = TestUtils.initChatService();
    }

    @Test
    @DisplayName("Check get by id")
    public void testGetById() {
        try {
            userService.create(new UserRegRequestDTO("test@gmail.com", "test", "test"));
            UserResponseDTO user = userService.getById(userService.getUserRepository().count());

            Assertions.assertEquals(userService.getUserRepository().count(), user.getId());
            Assertions.assertEquals("test", user.getUsername());
            Assertions.assertEquals(new HashMap<String, Long>(), user.getChats());

            log.info("Successfully checked getting by id");
        } catch (Exception e) {
            fail("Exception " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Check creation of user")
    public void testCreate() {
        UserRegRequestDTO userRegRequestDTO = new UserRegRequestDTO("test@gmail.com", "test", "test");

        try {
            User user = userService.create(userRegRequestDTO);

            Assertions.assertEquals((long) userService.getUserRepository().count(), user.getId());
            Assertions.assertEquals(userRegRequestDTO.getEmail(), user.getEmail());
            Assertions.assertEquals(userRegRequestDTO.getUsername(), user.getUsername());
            Assertions.assertFalse(user.isBlocked());
            Assertions.assertEquals(AppRole.ROLE_USER, user.getRole());

            log.info("Successfully checked creation of simple user");
        } catch (Exception e) {
            fail("Exception " + e.getMessage());
        }
    }


    @Test
    @DisplayName("Check creation of user")
    public void testCreateAdmin() {
        UserRegRequestDTO adminRegRequestDTO = new UserRegRequestDTO("admin@gmail.com", "admin", "admin");

        try {
            User admin = userService.createAdmin(adminRegRequestDTO);
            int counter = (int) userService.getUserRepository().count();


            Assertions.assertEquals((long) userService.getUserRepository().count(), admin.getId());
            Assertions.assertEquals(adminRegRequestDTO.getEmail(), admin.getEmail());
            Assertions.assertEquals(admin.getUsername(), admin.getUsername());
            Assertions.assertFalse(admin.isBlocked());
            Assertions.assertEquals(AppRole.ROLE_ADMIN, admin.getRole());

            log.info("Successfully checked creation of admin");
        } catch (Exception e) {
            fail("Exception " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Check delete user by yourself")
    public void testDeleteByYourSelf() {
        UserRegRequestDTO userRegRequestDTO = new UserRegRequestDTO("test@gmail.com", "test", "test");

        try {
            User user = userService.create(userRegRequestDTO);
            int counter = (int) userService.getUserRepository().count();

            userService.delete(TestUtils.getPrincipal("test@gmail.com"));
            int newCounter = (int) userService.getUserRepository().count();

            Assertions.assertNotEquals(counter, newCounter);

            log.info("Successfully checked deleting of user");
        } catch (Exception e) {
            fail("Exception " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Check deleting user by yourself")
    public void testDeleteByAdmin() throws Exception {
        UserRegRequestDTO userRegRequestDTO = new UserRegRequestDTO("user@gmail.com", "user", "user");
        UserRegRequestDTO adminRegRequestDTO = new UserRegRequestDTO("admin@gmail.com", "admin", "admin");
        User user = userService.create(userRegRequestDTO);
        User admin = userService.createAdmin(adminRegRequestDTO);
        int counter = (int) userService.getUserRepository().count();

        try {
           userService.delete(admin.getId(), TestUtils.getPrincipal(user.getEmail()));
        } catch (YouDontHavePermissionExceptiom e) {
            log.info("Successfully checked deleting other user by simple user");
        }

        try {
            userService.delete(user.getId(), TestUtils.getPrincipal("admin@gmail.com"));
            int newCounter = (int) userService.getUserRepository().count();

            Assertions.assertNotEquals(counter, newCounter);

            log.info("Successfully checked deleting of user by admin");
        } catch (Exception | YouDontHavePermissionExceptiom e) {
            fail("Exception " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Check updating user by yourself")
    public void testUpdate() {
        var userRegRequestDTO = new UserRegRequestDTO("user@gmail.com", "user", "user");
        var dto = new UserUpdateRequestDTO("Denis", "denis@gmail.com");

        var principal = TestUtils.getPrincipal(userRegRequestDTO.getEmail());
        try {
            var user = userService.create(userRegRequestDTO);
            var count = userService.getUserRepository().count();
            userService.update(dto, principal);
            var updatedUser = userService.getById(count);

            Assertions.assertNotEquals(dto.getEmail(), user.getEmail());
            Assertions.assertNotEquals(dto.getUsername(), updatedUser.getUsername());

            log.info("Successfully checked deleting of user by admin");
        } catch (Exception e) {
            fail("Exception " + e.getMessage());
        }
    }


    @Test
    @DisplayName("Check getting current user")
    public void testGetCurrent() {
        var userRegRequestDTO = new UserRegRequestDTO("user@gmail.com", "user", "user");
        var principal = TestUtils.getPrincipal(userRegRequestDTO.getEmail());

        try {
            User user = userService.create(userRegRequestDTO);
            UserResponseDTO responseDTO = userService.getCurrent(principal);

            Assertions.assertEquals(userService.getUserRepository().count(), responseDTO.getId());
            Assertions.assertEquals( "user", responseDTO.getUsername());
            Assertions.assertEquals(responseDTO.getChats(), new HashMap<String, Long>());

            log.info("Successfully checked deleting of user by admin");
        } catch (Exception e) {
            fail("Exception " + e.getMessage());
        }
    }
}
