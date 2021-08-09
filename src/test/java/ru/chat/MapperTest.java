package ru.chat;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.chat.dto.request.UserRegRequestDTO;
import ru.chat.entity.User;
import ru.chat.mapper.UserMapper;

@Slf4j
public class MapperTest {

    @Test
    void map() {
        UserRegRequestDTO dto = new UserRegRequestDTO("test@gmail.com", "username", "password");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        User user = Mappers.getMapper(UserMapper.class).create(dto);


        log.info(user.getEmail());
        log.info(user.getUsername());
        log.info(user.getPassword());

    }
}
