package ru.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String username;
    private Map<String, Long> chats;
}
