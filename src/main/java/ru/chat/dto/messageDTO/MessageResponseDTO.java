package ru.chat.dto.messageDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class MessageResponseDTO {
    private Long id;
    private String username;
    private String content;
    private Timestamp creationDate;
}
