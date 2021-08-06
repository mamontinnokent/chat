package ru.chat.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.chat.dto.response.MessageResponseDTO;
import ru.chat.entity.Chat;
import ru.chat.entity.Message;
import ru.chat.entity.User;
import ru.chat.entity.UserInChat;
import ru.chat.dto.response.ChatResponseDTO;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", expression = "java(ru.chat.entity.enums.ChatRole.ROLE_CREATOR)")
    @Mapping(target = "blocked", expression = "java(false)")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "chat", source = "chat")
    @Mapping(target = "inChat", expression = "java(true)")
    @Mapping(target = "kicked", expression = "java(false)")
    UserInChat create(User user, Chat chat);

    ChatResponseDTO getFromChat(Chat chat);

    MessageResponseDTO getFromMessage(Message msg);
}
