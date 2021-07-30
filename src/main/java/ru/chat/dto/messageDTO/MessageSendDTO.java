package ru.chat.dto.messageDTO;

import lombok.Data;

@Data
public class MessageSendDTO {
    private Long userId;
    private Long chatId;
    private String content;
}
