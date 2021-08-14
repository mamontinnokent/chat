package ru.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.chat.dto.request.MessageSendRequestDTO;
import ru.chat.service.chat_bot.ChatBotService;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;

import java.security.Principal;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("api/v1/bot")
@Tag(name = "Chat-bot controller", description = "Контроллер отвечает за логику работы с чат ботом")
public class ChatBotControllerV1 {

    private final ChatBotService chatBotService;

    @PostMapping
    @Operation(summary = "Выполнение команд")
    public ResponseEntity<?> send(@RequestBody MessageSendRequestDTO message, Principal principal) {
        try {
            return chatBotService.parser(message, principal);
        } catch (YouDontHavePermissionExceptiom e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Bad request", HttpStatus.BAD_REQUEST);
        }
    }

}
