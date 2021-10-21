package ru.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class ChatUpdateRequestDTO {
    @NotNull
    @Digits(integer = 19, fraction = 0)
    private Long id;
    @Size(min = 3)
    private String nameChat;
}
