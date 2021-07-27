package ru.chat.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.chat.service.UserService;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/user/")
public class UserControllerV1 {

    private final UserService userService;

    @GetMapping("{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping("all")
    public ResponseEntity<Object> all() {
        return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
    }

//    @DeleteMapping("delete")
//    public ResponseEntity<UserResponseDTO> delete(Principal principal) {
//        return ResponseEntity.ok(userService.getCurrentUser(principal));
//    }

    @GetMapping("get")
    public ResponseEntity<?> getCurrent(Principal principal, ModelMap model) {
        return null;
    }

//    @PutMapping("update")
//    public String update(@RequestBody UserDTO userDTO, Principal principal) {
//        return "";
//    }
}
