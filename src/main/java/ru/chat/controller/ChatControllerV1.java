package ru.chat.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.chat.dto.chatDTO.ChatCreateDTO;
import ru.chat.dto.chatDTO.ChatUpdateDTO;
import ru.chat.service.ChatService;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/chat/")
public class ChatControllerV1 {
    private final ChatService chatService;

    @PostMapping("create")
    public ResponseEntity<?> create(@RequestBody ChatCreateDTO chatDTO, Principal principal) {
        try {
            chatService.create(chatDTO, principal);
            return ResponseEntity.ok("Success");
        } catch (YouDontHavePermissionExceptiom e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllForThisUser(Principal principal) {
        return ResponseEntity.ok(chatService.getAllForThisUser(principal));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.get(id));
    }

    @GetMapping("getAll")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(chatService.getAll());
    }

    @DeleteMapping("{userInChatId}")
    public ResponseEntity<?> delete(@PathVariable Long userInChatId) {
        try {
            chatService.delete(userInChatId);
            return ResponseEntity.ok("Success");
        } catch (YouDontHavePermissionExceptiom youDontHavePermissionExceptiom) {
            return new ResponseEntity<>("You don't have permission", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/{userInChatId}/exit")
    public ResponseEntity<?> exit(@PathVariable Long userInChatId) {
        chatService.exit(userInChatId);
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/add/{chatId}")
    public ResponseEntity<?> add(Principal principal, Long chatId) {
        chatService.add(principal, chatId);
        return ResponseEntity.ok("Success");
    }

    @PostMapping("{id}/update")
    public ResponseEntity<?> update(@RequestBody ChatUpdateDTO dto) {
        chatService.update(dto);
        return ResponseEntity.ok("Success");
    }
}
