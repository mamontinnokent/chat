package ru.chat.controller;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.chat.dto.chatDTO.ChatCreateDTO;
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
        chatService.create(chatDTO, principal);
        return ResponseEntity.ok("Success");
    }

    @GetMapping
    public ResponseEntity<?> getAllForThisUser(Principal principal) {
        return ResponseEntity.ok(chatService.getAllForThisUser(principal));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.get(id));
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

//    @PostMapping("{id}/update")
//    public ResponseEntity<?> update(@RequestBody ChatCreateDTO chatCreateDTO) {
//        chatService.update(chatCreateDTO);
//        return ResponseEntity.ok(chatService.get(userInChatId));
//    }

}
