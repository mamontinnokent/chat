package ru.chat.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ChatResponseDTO {
    private String nameChat;
    private List<MessageResponseDTO> messages;
}
