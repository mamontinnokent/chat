package ru.chat.dto.request;

import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ChatUpdateRequestDTO {
    @NotNull
    @Digits(integer = 19, fraction = 0)
    private Long id;
    @Size(min = 3)
    private String nameChat;
    @Size(min = 3)
    private String caption;
}
