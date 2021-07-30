package ru.chat.dto.userDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class UserRegRequestDTO {
    @Email
    private String email;
    @Size(min = 4)
    private String username;
    @Size(min = 4)
    private String password;

}
