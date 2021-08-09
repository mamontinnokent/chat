package ru.chat.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.chat.dto.request.MessageSendRequestDTO;
import ru.chat.service.chat_bot.ChatBotService;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;

import java.io.IOException;
import java.security.Principal;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("api/v1/chat/")
@Tag(name = "Chat bot controller", description = "Контроллер отвечает за логику работы с чат ботом")
public class ChatBotContollerV1 {

    private final ChatBotService chatBotService;

    public ResponseEntity<?> send(@RequestBody MessageSendRequestDTO message, Principal principal) {
        try {
            String result = chatBotService.parser(message, principal);
            return ResponseEntity.ok(result);
        } catch (YouDontHavePermissionExceptiom e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return new ResponseEntity<>("Bad youtube request", HttpStatus.BAD_REQUEST);
        }
    }

}
