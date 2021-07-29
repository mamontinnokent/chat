package ru.chat.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.chat.dto.userDTO.UserUpdateDTO;
import ru.chat.service.UserService;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;

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
        // * удаление себя из приложение
        userService.delete(principal);
        return ResponseEntity.ok("Success");
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, Principal principal) {
        // * удаление любого (только для админа)
        try {
            userService.delete(id, principal);
            return ResponseEntity.ok("Success");
        } catch (YouDontHavePermissionExceptiom youDontHavePermissionExceptiom) {
            return new ResponseEntity<>("You can't delete other user", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping
    public ResponseEntity<?> getCurrent(Principal principal) {
        // * Получаем текущего юзера для профиля
        return ResponseEntity.ok(userService.getCurrent(principal));
    }

    @PutMapping("update")
    public ResponseEntity<?> update(@RequestBody UserUpdateDTO userDTO, Principal principal) {
        return ResponseEntity.ok(userService.update(userDTO, principal));
    }
}
