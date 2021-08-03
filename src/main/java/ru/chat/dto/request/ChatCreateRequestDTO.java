package ru.chat.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ChatCreateRequestDTO {
    @NotBlank
    private String name;
    @NotNull
    private String caption;
    @NotNull
    private boolean privacy;
}
