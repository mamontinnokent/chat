package ru.chat.dto.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
public class UserUpdateRequestDTO {
    @Size(min = 4)
    private String username;
    @Email
    private String email;
}
