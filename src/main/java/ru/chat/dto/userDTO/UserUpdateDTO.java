package ru.chat.dto.userDTO;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
public class UserUpdateDTO {
    @Size(min = 4)
    private String username;
    @Email
    private String email;
}
