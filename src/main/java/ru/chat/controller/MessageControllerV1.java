package ru.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.chat.dto.request.MessageSendRequestDTO;
import ru.chat.service.MessageService;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;

import javax.validation.Valid;
import java.security.Principal;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("message/")
@Tag(name = "Message controller", description = "Контроллер отвечает за логику работы с сообщениями")
public class MessageControllerV1 {

    private final MessageService messageService;

    @PostMapping("send")
    @Operation(summary = " Отправка сообщений")
    public ResponseEntity<?> send(@Valid @RequestBody MessageSendRequestDTO dto, Principal principal) {
        try {
            this.messageService.send(dto);
            return ResponseEntity.ok("Success");
        } catch (YouDontHavePermissionExceptiom e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("{userInChatId}/{messageId}")
    @Operation(summary = "Удаление сообщения")
    public ResponseEntity<?> delete(@Valid @PathVariable Long userInChatId, @PathVariable Long messageId) {
        try {
            this.messageService.delete(userInChatId, messageId);
            return ResponseEntity.ok("Success");
        } catch (YouDontHavePermissionExceptiom e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }
}
