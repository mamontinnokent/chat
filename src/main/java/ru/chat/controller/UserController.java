package ru.chat.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.chat.entity.User;
import ru.chat.service.UserService;

import java.security.Principal;

@Controller
@AllArgsConstructor
@RequestMapping("user/")
public class UserController {

    private final UserService userService;

    @GetMapping("{id}")
    public String getById(@PathVariable Long id, ModelMap model) {
        User user = userService.getById(id);
        model.addAttribute("user", user);
        return "get-user";
    }


    @DeleteMapping("delete/{id}")
    public String delete(@PathVariable Long id, ModelMap model) {
        User user = userService.getById(id);
        model.addAttribute("user", user);
        return "get-user";
    }

    @GetMapping("get")
    public String getCurrent(Principal principal, ModelMap model) {

        return "get-user";
    }

    @PutMapping("update")
    public String update(@RequestBody UserDTO userDTO, Principal principal) {
        return "";
    }
}
