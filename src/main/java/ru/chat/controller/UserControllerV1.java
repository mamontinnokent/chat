package ru.chat.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.chat.dto.userDTO.UpdateUserDTO;
import ru.chat.service.UserService;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/user/")
public class UserControllerV1 {

    private final UserService userService;

    @GetMapping("{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping("all")
    public ResponseEntity<?> all() {
        return ResponseEntity.ok(userService.getAll());
    }

    @DeleteMapping("delete")
    public ResponseEntity<?> delete(Principal principal) {
        userService.delete(principal);
        return ResponseEntity.ok("Success");
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok("Success");
    }

    @GetMapping
    public ResponseEntity<?> getCurrent(Principal principal) {
        return ResponseEntity.ok(userService.fromPrincipal(principal));
    }

    @PutMapping("update")
    public ResponseEntity<?> update(@RequestBody UpdateUserDTO userDTO, Principal principal) {
        return ResponseEntity.ok(userService.update(userDTO, principal));
    }
}
