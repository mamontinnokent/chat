package ru.chat.controller;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.chat.service.MessageService;

@Controller
@AllArgsConstructor
@RequestMapping("message/")
public class MessageController {

    private final MessageService messageService;



}
