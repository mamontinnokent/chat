package ru.chat.controller;


import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.chat.dto.chatDTO.ChatCreateDTO;
import ru.chat.service.ChatService;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/chat/")
public class ChatControllerV1 {
    private final ChatService chatService;

    @PostMapping("create")
    public ResponseEntity<?> create(@RequestBody ChatCreateDTO chatDTO, Principal principal) {
        chatService.create(chatDTO, principal);
        return ResponseEntity.ok("Success");
    }

    @GetMapping
    public ResponseEntity<?> getAllForThisUser(Principal principal) {
        return ResponseEntity.ok(chatService.getAllForThisUser(principal));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> get(@RequestParam Long id, Principal principal) {
        return ResponseEntity.ok(chatService.get());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@RequestParam Long id) {
    }

    @GetMapping("update/{id}")
    public ResponseEntity<?> update(@RequestParam Long id) {
        // ! some code
        return "";
    }

    @GetMapping("{id}")
    public ResponseEntity<?> get(@RequestParam Long id) {
        // ! some code
        return "";
    }
}
