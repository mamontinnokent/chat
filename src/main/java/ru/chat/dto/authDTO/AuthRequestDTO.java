package ru.chat.dto.authDTO;

import lombok.Data;

@Data
public class AuthRequestDTO {
    private String email;
    private String password;
}
