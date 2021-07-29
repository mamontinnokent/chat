package ru.chat.dto.chatDTO;

import lombok.Data;

@Data
public class ChatUpdateDTO {
    private Long id;
    private String nameChat;
    private String caption;
}
