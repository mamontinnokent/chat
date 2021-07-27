package ru.chat.dto.utilDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JWTTokenSuccessResponseDTO {
    private boolean success;
    private String token;
}
