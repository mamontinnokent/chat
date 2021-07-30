package ru.chat.dto.messageDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class MessageResponseDTO {
    private Long id;
    private String username;
    private String content;
    private Timestamp creationDate;
}
