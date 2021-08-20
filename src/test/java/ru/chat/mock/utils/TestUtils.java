package ru.chat.mock.utils;

import ru.chat.dto.request.UserRegRequestDTO;
import ru.chat.dto.response.ChatResponseDTO;
import ru.chat.dto.response.MessageResponseDTO;
import ru.chat.dto.response.UserResponseDTO;
import ru.chat.entity.Chat;
import ru.chat.entity.Message;
import ru.chat.entity.User;
import ru.chat.entity.UserInChat;
import ru.chat.entity.enums.AppRole;
import ru.chat.entity.enums.ChatRole;
import ru.chat.mapper.ChatMapper;
import ru.chat.mapper.UserMapper;
import ru.chat.mock.ChatRepositoryMock;
import ru.chat.mock.MessageRepositoryMock;
import ru.chat.mock.UserInChatRepositoryMock;
import ru.chat.mock.UserRepositoryMock;
import ru.chat.service.ChatService;
import ru.chat.service.MessageService;
import ru.chat.service.UserService;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TestUtils {
    private static ChatRepositoryMock chatRepositoryMock = new ChatRepositoryMock();
    private static UserRepositoryMock userRepositoryMock = new UserRepositoryMock();
    private static MessageRepositoryMock messageRepositoryMock = new MessageRepositoryMock();
    private static UserInChatRepositoryMock userInChatRepositoryMock = new UserInChatRepositoryMock();

    public static Principal getPrincipal(String email) {
        return new Principal() {
            @Override
            public String getName() {
                return email;
            }
        };
    }

    public static UserService initUserService() {
        UserMapper userMapper = new UserMapper() {
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

            public User createAdmin(UserRegRequestDTO dto) {
                if (dto == null) {
                    return null;
                } else {
                    User user = new User();
                    user.setUsername(dto.getUsername());
                    user.setEmail(dto.getEmail());
                    user.setRole(AppRole.ROLE_ADMIN);
                    user.setPassword(this.encodePassword(dto));
                    return user;
                }
            }

            public UserResponseDTO toUserResponseDTO(User user, List<UserInChat> list) {
                if (user == null && list == null) {
                    return null;
                } else {
                    Long id = null;
                    String username = null;
                    if (user != null) {
                        id = user.getId();
                        username = user.getUsername();
                    }

                    Map<String, Long> chats = this.mapChatsToHashMap(list);
                    UserResponseDTO userResponseDTO = new UserResponseDTO(id, username, chats);
                    return userResponseDTO;
                }
            }
        };

        return new UserService(
                userMapper,
                userRepositoryMock,
                userInChatRepositoryMock
        );
    }

    public static ChatService initChatService() {
        ChatMapper chatMapper = new ChatMapper() {
            public UserInChat create(User user, Chat chat) {
                if (user == null && chat == null) {
                    return null;
                } else {
                    UserInChat userInChat = new UserInChat();
                    if (user != null) {
                        userInChat.setUser(user);
                    }

                    if (chat != null) {
                        userInChat.setChat(chat);
                        List<Message> list = chat.getMessages();
                        if (list != null) {
                            userInChat.setMessages(new ArrayList(list));
                        }
                    }

                    userInChat.setRole(ChatRole.ROLE_CREATOR);
                    return userInChat;
                }
            }

            public ChatResponseDTO getFromChat(Chat chat) {
                if (chat == null) {
                    return null;
                } else {
                    ChatResponseDTO chatResponseDTO = new ChatResponseDTO();
                    chatResponseDTO.setNameChat(chat.getNameChat());
                    chatResponseDTO.setMessages(this.messageListToMessageResponseDTOList(chat.getMessages()));
                    return chatResponseDTO;
                }
            }

            public MessageResponseDTO getFromMessage(Message msg) {
                if (msg == null) {
                    return null;
                } else {
                    Long id = null;
                    String username = null;
                    String content = null;
                    Timestamp creationDate = null;
                    id = msg.getId();
                    username = msg.getUsername();
                    content = msg.getContent();
                    creationDate = msg.getCreationDate();
                    MessageResponseDTO messageResponseDTO = new MessageResponseDTO(id, username, content, creationDate);
                    return messageResponseDTO;
                }
            }

            protected List<MessageResponseDTO> messageListToMessageResponseDTOList(List<Message> list) {
                if (list == null) {
                    return null;
                } else {
                    List<MessageResponseDTO> list1 = new ArrayList(list.size());
                    Iterator var3 = list.iterator();

                    while (var3.hasNext()) {
                        Message message = (Message) var3.next();
                        list1.add(this.getFromMessage(message));
                    }

                    return list1;
                }
            }
        };

        return new ChatService(
                chatMapper,
                chatRepositoryMock,
                userRepositoryMock,
                userInChatRepositoryMock
        );
    }

    public static MessageService initMessageService() {
        return new MessageService(
                userRepositoryMock,
                chatRepositoryMock,
                messageRepositoryMock,
                userInChatRepositoryMock
        );
    }
}
