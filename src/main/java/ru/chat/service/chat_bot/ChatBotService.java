package ru.chat.service.chat_bot;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.chat.dto.request.ChatCreateRequestDTO;
import ru.chat.dto.request.MessageSendRequestDTO;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;

import java.security.Principal;

@Service
@AllArgsConstructor
public class ChatBotService {

    private static final String INFO =
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

    private final RoomOperate roomOperate;
    private final UserOperate userOperate;
    private final YouTubeOperate youTubeOperate;

    //  * Смотрим с чем операция и отправляем на функцию-исполнитель
    public String parser(MessageSendRequestDTO message, Principal principal) throws YouDontHavePermissionExceptiom {
        String operand = message.getContent().split(" ")[0];

        switch (operand) {
            case "//room":
                return this.roomOperate(message, principal);
            case "//user":
                return this.userOperate(message, principal);
            case "//yBot":
                return this.youTubeOperate(message, principal);
            case "//help":
                return INFO;
            default:
                return "Command not valid";
        }
    }

    //     * Комнаты:
    public String roomOperate(MessageSendRequestDTO message, Principal principal) throws YouDontHavePermissionExceptiom {
        String[] arrRequest = message.getContent().split(" ");
        String request = message.getContent();
        String operation = arrRequest[1];

        switch (operation) {
            case "create":
                if (arrRequest[2].equals("-c")) {
                    roomOperate.create(new ChatCreateRequestDTO(arrRequest[3], true), principal);
                    return "Success";
                }

                roomOperate.create(new ChatCreateRequestDTO(arrRequest[2], false), principal);
                return "Success";

            case "remove":
                if (!arrRequest[2].isBlank() && !arrRequest[2].isEmpty()) {
                    roomOperate.delete(arrRequest[2], principal);
                    return "Success";
                }
                return "Bad arrRequest";

            case "rename":
                String newName = arrRequest[2].split("||")[1];
                roomOperate.update(message.getChatId(), principal, newName);
                return "Success";

            case "connect":
                if (arrRequest[3].equals("-l")) {
                    roomOperate.addOtherUser(arrRequest[4], arrRequest[2], principal);
                    return "Success";
                }

                roomOperate.add(arrRequest[2], principal);
                return "Success";

            case "disconnect":
                if (arrRequest.length == 3) {
                    roomOperate.disconnect(message.getChatId(), principal);
                    return "Success";
                } else if (arrRequest.length == 5) {
                    roomOperate.disconnectOtherUser(arrRequest[2], arrRequest[4], principal);
                    return "Success";
                } else if (arrRequest.length == 7) {
                    // ! Доделать
                    roomOperate.disconnectOtherUserForValueMinutes(
                            /* имя чата  */ arrRequest[2],
                            /* имя юзера */ arrRequest[4],
                            /* количество минут */ Long.parseLong(arrRequest[6]),
                            principal);
                    return "Success";
                }

            default:
                return "Invalid room operation";
        }
    }

    public String userOperate(MessageSendRequestDTO message, Principal principal) throws YouDontHavePermissionExceptiom {
    }

    public String youTubeOperate(MessageSendRequestDTO message, Principal principal) throws YouDontHavePermissionExceptiom {
    }
}