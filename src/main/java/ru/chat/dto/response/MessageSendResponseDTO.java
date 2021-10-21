package ru.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageSendResponseDTO {
    private String content;
}
