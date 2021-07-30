package ru.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.chat.dto.authDTO.AuthRequestDTO;
import ru.chat.dto.userDTO.UserRegRequestDTO;
import ru.chat.dto.utilDTO.JWTTokenSuccessResponseDTO;
import ru.chat.security.JWTTokenProvider;
import ru.chat.security.SecurityConstants;
import ru.chat.service.UserService;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication controller", description = "Контроллер отвечает за регистрацию и авторизацию пользователей")
public class AuthControllerV1 {

    private final UserService userService;
    private final JWTTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/signin")
    @Operation(summary = "Авторизация пользователя")
    public ResponseEntity<?> authenticate(@RequestBody AuthRequestDTO requestDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    requestDTO.getEmail(), requestDTO.getPassword()
            ));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = SecurityConstants.TOKEN_PREFIX + jwtTokenProvider.generateToken(authentication);
            log.info("Пользователь {} был авторизован", requestDTO.getEmail());
            return ResponseEntity.ok(new JWTTokenSuccessResponseDTO(true, jwt));
        } catch (Exception e) {
            return new ResponseEntity<>("User doesn't exists", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/signup")
    @Operation(summary = "Регистрация пользователя")
    public ResponseEntity<Object> create(@RequestBody UserRegRequestDTO requestDTO) {
        try {
            this.userService.create(requestDTO);
            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            return new ResponseEntity<>("User with this credentials already exists", HttpStatus.BAD_REQUEST);
        }
    }
}
