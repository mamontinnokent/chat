package ru.chat.dto.messageDTO;

import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MessageSendDTO {
    @NotNull
    @Digits(integer = 19, fraction = 0)
    private Long userId;
    @NotNull
    @Digits(integer = 19, fraction = 0)
    private Long chatId;
    @NotBlank
    private String content;
}
