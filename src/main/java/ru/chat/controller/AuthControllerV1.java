package ru.chat.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.chat.dto.authDTO.AuthRequestDTO;
import ru.chat.dto.userDTO.RegUserDTO;
import ru.chat.dto.utilDTO.JWTTokenSuccessResponseDTO;
import ru.chat.security.JWTTokenProvider;
import ru.chat.security.SecurityConstants;
import ru.chat.service.UserService;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
@Api(value = "User")
public class AuthControllerV1 {

    private final UserService userService;
    private final JWTTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/signin")
    @ApiOperation(value = "Login")
    public ResponseEntity<?> authenticate(@RequestBody AuthRequestDTO requestDTO) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                requestDTO.getEmail(), requestDTO.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = SecurityConstants.TOKEN_PREFIX + jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JWTTokenSuccessResponseDTO(true, jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> create(@RequestBody RegUserDTO requestDTO) {
        userService.create(requestDTO);
        return ResponseEntity.ok("Success");
    }
}
