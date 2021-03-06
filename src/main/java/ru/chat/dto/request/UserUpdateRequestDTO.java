package ru.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class UserUpdateRequestDTO {
    @Size(min = 4)
    private String username;
    @Email
    private String email;
}
