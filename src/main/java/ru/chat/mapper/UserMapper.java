package ru.chat.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.chat.dto.userDTO.RegUserDTO;
import ru.chat.dto.userDTO.UserResponseDTO;
import ru.chat.entity.User;
import ru.chat.entity.UserInChat;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", expression = "java(this.encodePassword(dto))")
    User create(RegUserDTO dto);

    @Mapping(target = "chats", expression = "java(this.mapChatsToHashMap(user.getChats()))")
    UserResponseDTO toUserResponseDTO(User user);

    default String encodePassword(RegUserDTO dto) {
        return (new BCryptPasswordEncoder(12)).encode(dto.getPassword());
    }

    default Map<String, Long> mapChatsToHashMap(List<UserInChat> chats) {
        return chats.stream().collect(Collectors.toMap(k -> k.getChat().getNameChat(), v -> v.getChat().getId()));
    }

}
