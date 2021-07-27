package ru.chat.dto.userDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegUserDTO {

    private String email;
    private String username;
    private String password;

}
