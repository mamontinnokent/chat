package ru.chat.dto.chatDTO;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ChatCreateDTO {
    @NotBlank
    private String name;
    @NotNull
    private String caption;
    @NotNull
    private boolean privacy;
}
