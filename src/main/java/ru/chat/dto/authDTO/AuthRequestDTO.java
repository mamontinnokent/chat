package ru.chat.dto.authDTO;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class AuthRequestDTO {
    @Email
    private String email;
    @NotBlank
    private String password;
}
