package ru.chat.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.chat.dto.messageDTO.MessageSendDTO;
import ru.chat.service.MessageService;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping("message/")
public class MessageControllerV1 {

    private final MessageService messageService;


    @PostMapping("send/{chatId}")
    public String create(@PathVariable Long chatId, Principal principal, MessageSendDTO dto) {
    }

    @DeleteMapping("delete/{id}")
    public String delete(@RequestParam Long id) {
        return ;
    }

    @GetMapping("update/{id}")
    public String update(@RequestParam Long id) {
        return ;
    }

    @GetMapping("{id}")
    public String get(@RequestParam Long id) {
        return ;
    }

}
