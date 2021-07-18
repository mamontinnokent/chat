package ru.chat.controller;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.chat.service.MessageService;

import java.security.Principal;

@Controller
@AllArgsConstructor
@RequestMapping("message/")
public class MessageController {

    private final MessageService messageService;


    @PostMapping("create")
    public String create(ChatDTO chatDTO, Principal principal) {
        // ! some code
        return "";
    }
    @DeleteMapping("delete/{id}")
    public String delete(@RequestParam Long id) {
        // ! some code
        return "";
    }

    @GetMapping("update/{id}")
    public String update(@RequestParam Long id) {
        // ! some code
        return "";
    }

    @GetMapping("{id}")
    public String get(@RequestParam Long id) {
        // ! some code
        return "";
    }

}
