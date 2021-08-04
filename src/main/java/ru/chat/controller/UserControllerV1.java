package ru.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.chat.dto.request.UserUpdateRequestDTO;
import ru.chat.service.UserService;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;

import javax.validation.Valid;
import java.security.Principal;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("api/v1/user/")
@Tag(name = "User controller", description = "Контроллер отвечает за логику работы с пользователем")
public class UserControllerV1 {

    private final UserService userService;

    @GetMapping("{id}")
    @Operation(summary = "Получение пользователя по id")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(this.userService.getById(id));
    }

    @GetMapping("all")
    @Operation(summary = "Получение всех пользователей для поиска")
    public ResponseEntity<?> all() {
        return ResponseEntity.ok(this.userService.getAll());
    }

    @DeleteMapping("delete")
    @Operation(summary = "Удаление текщего пользователя")
    public ResponseEntity<?> delete(Principal principal) {
        this.userService.delete(principal);
        return ResponseEntity.ok("Success");
    }

    @DeleteMapping("delete/{id}")
    @Operation(
            summary = "Удаление пользователя",
            description = "Админ может удалить любого пользователя"
    )
    public ResponseEntity<?> delete(@PathVariable Long id, Principal principal) {
        try {
            this.userService.delete(id, principal);
            return ResponseEntity.ok("Success");
        } catch (YouDontHavePermissionExceptiom e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping
    @Operation(summary = "Получение текущего пользователей")
    public ResponseEntity<?> getCurrent(Principal principal) {
        return ResponseEntity.ok(this.userService.getCurrent(principal));
    }

    @PostMapping("update")
    @Operation(
            summary = "Обновление данных пользователя",
            description = "Если вдруг захочется поменять юзернейм или почту"
    )
    public ResponseEntity<?> update(@Valid @RequestBody UserUpdateRequestDTO userDTO, Principal principal) {
        return ResponseEntity.ok(this.userService.update(userDTO, principal));
    }
}
