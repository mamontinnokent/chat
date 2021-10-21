package ru.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class MessageSendRequestDTO {
    @NotNull
    @Digits(integer = 19, fraction = 0)
    private Long userId;
    @NotNull
    @Digits(integer = 19, fraction = 0)
    private Long chatId;
    @NotBlank
    private String content;
}
