package ru.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ChatCreateRequestDTO {
    @NotBlank
    private String name;
    @NotNull
    private boolean privacy;
}
