package ru.chat.controller;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.chat.service.UserService;

@Controller
@AllArgsConstructor
@RequestMapping("auth/")
public class AuthController {

    private UserService userService;

    @RequestMapping("signup/")
    public String signup(@RequestBody SignupDTO signupDTO) {
        return "";
    }

    @RequestMapping("signup/")
    public String signin(@RequestBody) {
        return "";
    }
}
