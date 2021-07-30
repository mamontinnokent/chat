package ru.chat.dto.chatDTO;

import lombok.Data;
import ru.chat.dto.messageDTO.MessageResponseDTO;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ChatResponseDTO {
    private String nameChat;
    private List<MessageResponseDTO> messages;
}
