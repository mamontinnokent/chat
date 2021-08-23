package ru.chat.service.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
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

import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TestUtils {

    public List<User> userList;
    public List<Chat> chatList;
    public List<UserInChat> userInChatList;

    public TestUtils() {
        setUsers();
        setChats();

    }

    public UserMapper getUserMapper() {
        return new UserMapper() {
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

            @Override
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
    }

    public ChatMapper getChatMapper() {
        return new ChatMapper() {
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

            @Override
            public UserInChat addToChat(User user, Chat chat) {
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

                    userInChat.setRole(ChatRole.ROLE_USER);
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

                    while(var3.hasNext()) {
                        Message message = (Message)var3.next();
                        list1.add(this.getFromMessage(message));
                    }

                    return list1;
                }
            }
        };
    }

    public Principal getPrincipal(String email) {
        return new Principal() {
            @Override
            public String getName() {
                return email;
            }
        };
    }

    public void setUsers() {
        UserMapper mapper = getUserMapper();
        userList = List.of(
                mapper.createAdmin(new UserRegRequestDTO("admin@gmail.com", "admin", "admin")).setId(1l),
                mapper.create(new UserRegRequestDTO("test1@gmail.com", "test1", "test")).setId(2l),
                mapper.create(new UserRegRequestDTO("test2@gmail.com", "test2", "test")).setId(3l),
                mapper.create(new UserRegRequestDTO("test3@gmail.com", "test3", "test")).setId(4l),
                mapper.create(new UserRegRequestDTO("test4@gmail.com", "test4", "test")).setId(5l),
                mapper.create(new UserRegRequestDTO("test5@gmail.com", "test5", "test")).setId(6l)
        );
    }

    private void setChats() {
        var mapper = getChatMapper();
        chatList = List.of(
            new Chat("test_chat_open_1", false),
            new Chat("test_chat_open_2", false),
            new Chat("test_chat_open_3", false),
            new Chat("test_chat_close_1", true),
            new Chat("test_chat_close_2", true),
            new Chat("test_chat_close_3", true)
        );

        AtomicInteger iterator = new AtomicInteger(0);
        userInChatList = chatList.stream().map(chat -> {
            var i = iterator.getAndIncrement();
            chat.setId((long) (i + 1));

            var user = this.userList.get(i);
            var userAndChat = mapper.create(user, chat);
            userAndChat.onCreate();
            user.getChats().add(userAndChat);

            return userAndChat;
        }).collect(Collectors.toList());


        userList.forEach(user -> {
            var itr = new AtomicInteger(1);
            chatList.forEach(chat -> {
                if (!doYouIn(chat, user)) {
                    var newUserInChat = mapper.addToChat(user, chat)
                            .setId((long) userInChatList.size() + 1l);
                    newUserInChat.onCreate();

                    if (user.getRole() == AppRole.ROLE_ADMIN)
                        newUserInChat.setRole(ChatRole.ROLE_ADMIN);

                    userInChatList.add(newUserInChat);
                }
            });
        });
    }

    public List<UserInChat> getById(Long id, Boolean inChat) {
        var user = userList.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        log.info(userInChatList.toString());
        log.info(chatList.toString());
        return userInChatList
                .stream()
                .filter(uic -> uic.getUser().equals(user) && inChat.equals(uic.isInChat()))
                .collect(Collectors.toList());
    }

    private boolean doYouIn(Chat chat, User user) {
        return userInChatList
                .stream()
                .anyMatch(uic -> uic.getChat().equals(chat) && uic.getUser().equals(user));
    }
}
