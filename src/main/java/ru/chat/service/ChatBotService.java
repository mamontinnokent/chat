package ru.chat.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.chat.dto.request.ChatCreateRequestDTO;
import ru.chat.dto.response.BotMessageRequestDTO;
import ru.chat.entity.User;
import ru.chat.repository.UserRepository;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;

import java.security.Principal;

@Service
@AllArgsConstructor
public class ChatBotService {

    public static final String INFO =
            "Комнаты:\n" +
                    "1. //room create {Название комнаты} - создает комнаты;\n" +
                    "-c закрытая комната. Только (владелец, модератор и админ) может\n" +
                    "добавлять/удалять пользователей из комнаты.\n" +
                    "2. //room remove {Название комнаты} - удаляет комнату (владелец и админ);\n" +
                    "3. //room rename {Название комнаты}||{Новое название} - переименование комнаты (владелец и\n" +
                    "админ);\n" +
                    "4. //room connect {Название комнаты} - войти в комнату;\n" +
                    "-l {login пользователя} - добавить пользователя в комнату\n" +
                    "5. //room disconnect - выйти из текущей комнаты;\n" +
                    "6. //room disconnect {Название комнаты} - выйти из заданной комнаты;\n" +
                    "-l {login пользователя} - выгоняет пользователя из комнаты (для владельца,\n" +
                    "модератора и админа).\n" +
                    "-m {Количество минут} - время на которое пользователь не сможет войти (для\n" +
                    "владельца, модератора и админа).\n" +
                    "Пользователи:\n" +
                    "1. //user rename {login пользователя} (владелец и админ);\n" +
                    "2. //user ban;\n" +
                    "-l {login пользователя} - выгоняет пользователя из всех комнат\n" +
                    "-m {Количество минут} - время на которое пользователь не сможет войти.\n" +
                    "3. //user moderator {login пользователя} - действия над модераторами.\n" +
                    "-n - назначить пользователя модератором.\n" +
                    "-d - “разжаловать” пользователя.\n" +
                    "Боты:\n" +
                    "1. //yBot find -k -l {название канала}||{название видео} - в ответ бот присылает\n" +
                    "ссылку на ролик;\n" +
                    "-v - выводит количество текущих просмотров.\n" +
                    "-l - выводит количество лайков под видео.\n" +
                    "2. //yBot help - список доступных команд для взаимодействия.\n";
    private final YouTubeBot youTubeBot;
    private final ChatService chatService;
    private final UserService userService;
    private final UserRepository userRepository;

    // * Получаем текущего пользователя, утилитарный метод
    private User fromPrincipal(Principal principal) {
        return this.userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }



//  * Смотрим с чем операция и отправляем на функцию-исполнитель
    public String operate(BotMessageRequestDTO message, Principal principal) throws YouDontHavePermissionExceptiom {
        String operand = message.getMessage().split(" ")[0];

        switch (operand) {
            case "//room":
                return this.roomOperate(message, principal);
            case "//user":
            case "//yBot":
                break;
            case "//help":
                return INFO;
            default:
                return "Command not valid";
        }
        return null;
    }

//  * Комнаты:
//  * 1. //room create {Название комнаты} - создает комнаты; -c закрытая комната.
//  *    Только (владелец, модератор и админ) может
//  *    добавлять/удалять пользователей из комнаты.
//  * 2. //room remove {Название комнаты} - удаляет комнату (владелец и админ);
//  * 3. //room rename {Название комнаты}||{Новое название} - переименование комнаты (владелец и админ);
//  * 4. //room connect {Название комнаты} - войти в комнату;
//  *    -l {login пользователя} - добавить пользователя в комнату (//room connect {Название комнаты} -l {login})
//  * 5. //room disconnect - выйти из текущей комнаты;
//  * 6. //room disconnect {Название комнаты} - выйти из заданной комнаты;
//  *    -l {login пользователя} - выгоняет пользователя из комнаты (для владельца, модератора и админа).
//  *    -m {Количество минут} - время на которое пользователь не сможет войти (для владельца, модератора и админа).
    public String roomOperate(BotMessageRequestDTO message, Principal principal) throws YouDontHavePermissionExceptiom {
        String[] request = message.getMessage().split(" ");
        String operation = request[1];

        switch (operation) {
            case "create":
                if (request[2].equals("-c") && !request[3].isBlank() && !request[3].isEmpty()) {
                    chatService.create(new ChatCreateRequestDTO(request[3], true), principal);
                    return "Success";
                }

                chatService.create(new ChatCreateRequestDTO(request[2], false), principal);
                return "Success";

            case "remove":
                if (!request[2].isBlank() && !request[2].isEmpty()) {
                    chatService.delete(request[2], principal);
                    return "Success";
                }

            case "rename":
                String newName = request[2].split("||")[1];
                chatService.update(request[2].split("||")[0], principal);
                return "Success";

            case "connect":
                if (request[3].equals("-l")) {
                    var user = userRepository.findByUsername(request[4])
                            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                }

                chatService.add(request[2], principal);
                return "Success";

            case "":
        }
        return null;
    }
}
