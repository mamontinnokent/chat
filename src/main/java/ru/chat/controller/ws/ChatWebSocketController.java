package ru.chat.controller.ws;

import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;
import ru.chat.dto.request.MessageSendRequestDTO;
import ru.chat.service.MessageService;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;

@RestController
@AllArgsConstructor
public class ChatWebSocketController {

    private final MessageService messageService;

    @MessageMapping("/send")
    @SendTo("/topic/chat")
    public MessageSendRequestDTO messaging(@DestinationVariable Long chatId, MessageSendRequestDTO message) throws Exception {
        try {
            return this.messageService.update(message);
        } catch (YouDontHavePermissionExceptiom e) {
            return null;
        }

    }
}
