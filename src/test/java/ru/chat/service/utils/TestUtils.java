package ru.chat.service.utils;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class TestUtils {

    private int iterator = 0;

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

    public User getUser(Long id, AppRole role, String email) {
        return new User()
                .setId(id)
                .setUsername("user" + id)
                .setEmail(email)
                .setPassword(new BCryptPasswordEncoder(12).encode("test"))
                .setBlocked(false)
                .setRole(role);
    }

    public UserInChat getUIC(Long id, ChatRole role, Chat chat, User user) {
        return new UserInChat()
                .setId(id)
                .setInChat(true)
                .setBlockedTime(Timestamp.valueOf(LocalDateTime.now()))
                .setKickedTime(Timestamp.valueOf(LocalDateTime.now()))
                .setUser(user)
                .setChat(chat)
                .setRole(role);
    }

    public Chat createChat(Long id, User creator, boolean privacy) {
        var chat = new Chat()
                .setId(id)
                .setNameChat("chat" + id)
                .setPrivacy(privacy)
                .setCreationDate(Timestamp.valueOf(LocalDateTime.now()));

        var uic = getUIC(1l, ChatRole.ROLE_CREATOR, chat, creator);

        chat.getMembers().add(uic);
        creator.getChats().add(uic);
        return chat;
    }
}
