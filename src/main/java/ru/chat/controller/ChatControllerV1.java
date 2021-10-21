package ru.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.chat.dto.request.ChatCreateRequestDTO;
import ru.chat.dto.request.ChatUpdateRequestDTO;
import ru.chat.service.ChatService;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;

import javax.validation.Valid;
import java.security.Principal;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("api/v1/chat/")
@Tag(name = "Chat controller", description = "Контроллер отвечает за логику работы с чатами")
public class ChatControllerV1 {

    private final ChatService chatService;

    @PostMapping("create")
    @Operation(summary = "Создание чата")
    public ResponseEntity<?> create(@Valid @RequestBody ChatCreateRequestDTO chatDTO, Principal principal) {
        try {
            this.chatService.create(chatDTO, principal);
            return ResponseEntity.ok("Success");
        } catch (YouDontHavePermissionExceptiom e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping
    @Operation(summary = "Все чаты для текущего пользователя")
    public ResponseEntity<?> getAllForThisUser(Principal principal) {
        return ResponseEntity.ok(this.chatService.getAllForCurrent(principal));
    }

    @GetMapping("{id}")
    @Operation(summary = "Получение чата по id")
    public ResponseEntity<?> get(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(this.chatService.get(id, principal));
    }

    @GetMapping("all")
    @Operation(summary = "Получение всех чатов")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(this.chatService.getAll());
    }

    @DeleteMapping("{userInChatId}")
    @Operation(summary = "Удаление чата")
    public ResponseEntity<?> delete(@PathVariable Long userInChatId) {
        try {
            this.chatService.delete(userInChatId);
            return ResponseEntity.ok("Success");
        } catch (YouDontHavePermissionExceptiom e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("{userInChatId}/exit")
    @Operation(summary = "Выход из чата")
    public ResponseEntity<?> exit(@PathVariable Long userInChatId) {
        this.chatService.exit(userInChatId);
        return ResponseEntity.ok("Success");
    }

    @GetMapping("add/{chatId}")
    @Operation(summary = "Вход в чат")
    public ResponseEntity<?> add(@PathVariable Long chatId, Principal principal) {
        try {
            this.chatService.add(principal, chatId);
            return ResponseEntity.ok("Success");
        } catch (YouDontHavePermissionExceptiom e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("update")
    @Operation(summary = "Обновление данных чата")
    public ResponseEntity<?> update(@Valid @RequestBody ChatUpdateRequestDTO dto, Principal principal) {
        try {
            this.chatService.update(dto, principal);
            return ResponseEntity.ok("Success");
        } catch (YouDontHavePermissionExceptiom e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("moderator/{userInChatId}")
    @Operation(summary = "Назначение модератора чата")
    public ResponseEntity<?> setModerator(@PathVariable Long userInChatId, Principal principal) {
        try {
            this.chatService.setModerator(userInChatId, principal);
            return ResponseEntity.ok("Success");
        } catch (YouDontHavePermissionExceptiom e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("block/{userInChatId}")
    @Operation(summary = "Блокирование пользователя")
    public ResponseEntity<?> block(@PathVariable Long userInChatId, Principal principal) {
        try {
            this.chatService.block(userInChatId, principal);
            return ResponseEntity.ok("Success");
        } catch (YouDontHavePermissionExceptiom e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

}
