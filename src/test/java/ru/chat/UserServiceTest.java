package ru.chat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.chat.dto.request.UserRegRequestDTO;
import ru.chat.dto.response.UserResponseDTO;
import ru.chat.entity.User;
import ru.chat.mapper.UserMapper;
import ru.chat.mock.UserInChatRepositoryMock;
import ru.chat.mock.UserRepositoryMock;
import ru.chat.service.UserService;

import java.util.Map;

import static org.fest.assertions.Fail.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {

    public UserService initUserService() {
        UserMapper userMapper = new UserMapper() {
            @Override
            public User create(UserRegRequestDTO dto) {
                if (dto == null) {
                    return null;
                } else {
                    User user = new User();
                    user.setUsername(dto.getUsername());
                    user.setEmail(dto.getEmail());
                    user.setPassword(this.encodePassword(dto));
                    return user;
                }
            }

            @Override
            public UserResponseDTO toUserResponseDTO(User user) {
                if (user == null) {
                    return null;
                } else {
                    Long id = null;
                    String username = null;
                    id = user.getId();
                    username = user.getUsername();
                    Map<String, Long> chats = this.mapChatsToHashMap(user.getChats());
                    UserResponseDTO userResponseDTO = new UserResponseDTO(id, username, chats);
                    return userResponseDTO;
                }
            }
        };

         return new UserService(
                    userMapper,
                    new UserRepositoryMock(),
                    new UserInChatRepositoryMock()
            );
    }

    @Test
    @DisplayName("Check creation of user")
    public void testCreate() {
        UserService userService = initUserService();
        UserRegRequestDTO userRegRequestDTO = new UserRegRequestDTO("test@gmail.com", "test", "test");

        try {
            User user1 = new User();
            User user2 = userService.create(userRegRequestDTO);

            user1.setId(1L);
            user1.setUsername(userRegRequestDTO.getUsername());
            user1.setEmail(userRegRequestDTO.getEmail());
            user1.setPassword(user2.getPassword());

            Assertions.assertEquals(user1, user2);
        } catch (Exception e) {
            fail("Exception " + e.getMessage());
        }
    }



}
