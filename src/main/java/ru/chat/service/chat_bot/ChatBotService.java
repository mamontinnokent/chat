package ru.chat.service.chat_bot;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.chat.dto.request.ChatCreateRequestDTO;
import ru.chat.dto.request.MessageSendRequestDTO;
import ru.chat.dto.response.MessageSendResponseDTO;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ChatBotService {

    private static final ResponseEntity<List<MessageSendResponseDTO>> DEFAULT_SUCCESS_RESPONSE = ResponseEntity.ok(List.of(new MessageSendResponseDTO("Succes")));
    private static final ResponseEntity<List<MessageSendResponseDTO>> DEFAULT_BAD_RESPONSE = new ResponseEntity(List.of(new MessageSendResponseDTO("Command don't valid.")), HttpStatus.BAD_REQUEST);
    private static final String LINK_FORM = "https://www.youtube.com/watch?v=";
    private static final String YOUTUBE_INFO =
            "//yBot find {название канала}||{название видео} - в ответ бот присылает ссылку на ролик\n" +
                    "-v - выводит количество текущих просмотров. //yBot find {название канала}||{название видео} -v\n" +
                    "-l - выводит количество лайков под видео. //yBot find {название канала}||{название видео} -l\n" +
                    "2. //yBot changelInfo {имя канала}. - Первым сообщением от бота выводится имя канала, вторым - ссылки на последние 5 роликов\n" +
                    "3. //yBot videoCommentRanom {имя канала}||{Название ролика} - Среди комментариев к ролику рандомно выбирается 1 - " +
                    "Первым сообщением бот выводит login человека, который оставил этот комментарий - Вторым сообщением бот выводит сам комментарий\n";

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
                    "1. //yBot find {название канала}||{название видео} - в ответ бот присылает\n" +
                    "ссылку на ролик;\n" +
                    "-v - выводит количество текущих просмотров.\n" +
                    "-l - выводит количество лайков под видео.\n" +
                    "2. //yBot help - список доступных команд для взаимодействия.\n" +
                    "3. //yBot channelInfo {имя канала}. - Первым сообщением от бота выводится имя канала, вторым - ссылки на последние 5 роликов\n" +
                    "4. //yBot videoCommentRadnom {имя канала}||{Название ролика} - Среди комментариев к ролику рандомно выбирается 1 - " +
                    "Первым сообщением бот выводит login человека, который оставил этот комментарий - Вторым сообщением бот выводит сам комментарий\n";

    private final RoomOperate roomOperate;
    private final UserOperate userOperate;
    private final YouTubeOperate youTubeOperate;

    //  * Смотрим с чем операция и отправляем на функцию-исполнитель
    public ResponseEntity<List<MessageSendResponseDTO>> parser(MessageSendRequestDTO message, Principal principal) throws YouDontHavePermissionExceptiom, IOException {
        String operand = message.getContent().split(" ")[0];

        switch (operand) {
            case "//room":
                return this.room(message, principal);
            case "//user":
                return this.user(message, principal);
            case "//yBot":
                return this.youTube(message, principal);
            case "//help":
                return ResponseEntity.ok(List.of(new MessageSendResponseDTO(INFO)));
            default:
                return DEFAULT_BAD_RESPONSE;
        }
    }

    //   * Комнаты:
    //   * 1. //room create {Название комнаты} - создает комнаты
    //   *      -c закрытая комната. Только (владелец, модератор и админ) может добавлять/удалять пользователей из комнаты
    //   * 2. //room remove {Название комнаты} - удаляет комнату (владелец и админ)
    //   * 3. //room rename {Название комнаты}||{Новое название} - переименование комнаты (владелец и админ)
    //   * 4. //room connect {Название комнаты} - войти в комнату
    //   *      -l {login пользователя} - добавить пользователя в комнату
    //   * 5. //room disconnect - выйти из текущей комнаты
    //   * 6. //room disconnect {Название комнаты} - выйти из заданной комнаты
    //   *      -l {login пользователя} - выгоняет пользователя из комнаты (для владельца, модератора и админа).
    //   *      -m {Количество минут} - время на которое пользователь не сможет войти (для владельца, модератора и админа).
    private ResponseEntity<List<MessageSendResponseDTO>> room(MessageSendRequestDTO message, Principal principal) throws YouDontHavePermissionExceptiom {
        String[] arrRequest = message.getContent().split(" ");
        String request = message.getContent();
        String operation = arrRequest[1];

        switch (operation) {
            case "create":
                if (arrRequest[2].equals("-c")) {
                    this.roomOperate.create(new ChatCreateRequestDTO(arrRequest[3], true), principal);
                    return DEFAULT_SUCCESS_RESPONSE;
                }

                this.roomOperate.create(new ChatCreateRequestDTO(arrRequest[2], false), principal);
                return DEFAULT_SUCCESS_RESPONSE;

            case "remove":
                if (!arrRequest[2].isBlank() && !arrRequest[2].isEmpty()) {
                    this.roomOperate.delete(arrRequest[2], principal);
                    return DEFAULT_SUCCESS_RESPONSE;
                }
                return new ResponseEntity(List.of(new MessageSendResponseDTO("Bad request")), HttpStatus.BAD_REQUEST);

            case "rename":
                String newName = arrRequest[2].split("||")[1];
                this.roomOperate.update(message.getChatId(), principal, newName);
                return DEFAULT_SUCCESS_RESPONSE;

            case "connect":
                if (arrRequest[3].equals("-l")) {
                    this.roomOperate.addOtherUser(arrRequest[4], arrRequest[2], principal);
                    return DEFAULT_SUCCESS_RESPONSE;
                }

                this.roomOperate.add(arrRequest[2], principal);
                return DEFAULT_SUCCESS_RESPONSE;

            case "disconnect":
                if (arrRequest.length == 3) {
                    this.roomOperate.disconnect(message.getChatId(), principal);
                    return DEFAULT_SUCCESS_RESPONSE;
                } else if (arrRequest.length == 5) {
                    this.roomOperate.disconnectOtherUser(arrRequest[2], arrRequest[4], principal);
                    return DEFAULT_SUCCESS_RESPONSE;
                } else if (arrRequest.length == 7) {
                    this.roomOperate.disconnectOtherUserForValueMinutes(arrRequest[2], arrRequest[4], Long.parseLong(arrRequest[6]), principal);
                    return DEFAULT_SUCCESS_RESPONSE;
                }

            default:
                return DEFAULT_BAD_RESPONSE;
        }
    }

    //   * Пользователи:
    //   * 1. //user rename {login пользователя} (владелец и админ)
    //   * 2. //user ban
    //   *      l {login пользователя} - выгоняет пользователя из всех комнат
    //   *      m {Количество минут} - время на которое пользователь не сможет войти.
    //   * 3. //user moderator {login пользователя} - действия над модераторами.
    //   *      -n - назначить пользователя модератором.
    //   *      -d - “разжаловать” пользователя.
    private ResponseEntity<List<MessageSendResponseDTO>> user(MessageSendRequestDTO message, Principal principal) throws YouDontHavePermissionExceptiom {
        var arrRequest = message.getContent().split(" ");
        var request = message.getContent();
        var operation = arrRequest[1];

        switch (operation) {
            case "rename":
                String[] names = arrRequest[2].split("||");
                this.userOperate.rename(names[0], names[1], principal);
                return DEFAULT_SUCCESS_RESPONSE;

            case "ban":
                if (arrRequest.length == 4 && arrRequest[2].equals("-l")) {
                    this.userOperate.ban(principal, arrRequest[3]);
                    return DEFAULT_SUCCESS_RESPONSE;
                } else if (arrRequest.length == 4 && arrRequest[2].equals("-l") && arrRequest[4].equals("-m")) {
                    this.userOperate.ban(principal, arrRequest[3], Long.parseLong(arrRequest[5]));
                    return DEFAULT_SUCCESS_RESPONSE;
                }

            case "moderator":
                boolean doYouModerator = false;
                String[] names1 = arrRequest[2].split("||");

                if (arrRequest[3].equals("-d"))
                    doYouModerator = true;
                this.userOperate.setModerator(names1[0], names1[1], principal, doYouModerator);

                return DEFAULT_SUCCESS_RESPONSE;

            default:
                return DEFAULT_BAD_RESPONSE;
        }
    }

    //   * 1. //yBot find {название канала}||{название видео} - в ответ бот присылает ссылку на ролик
    //   *           -v - выводит количество текущих просмотров.
    //   *           -l - выводит количество лайков под видео.
    //   * 2. //yBot help - список доступных команд для взаимодействия.
    //   * 3. //yBot channelInfo {имя канала}. - Первым сообщением от бота выводится имя канала, вторым - ссылки на последние 5 роликов
    //   * 4. //yBot videoCommentRandom {имя канала}||{Название ролика} - Среди комментариев к ролику рандомно выбирается 1 -
    //   *           Первым сообщением бот выводит login человека, который оставил этот комментарий - Вторым сообщением бот выводит сам комментарий
    private ResponseEntity<List<MessageSendResponseDTO>> youTube(MessageSendRequestDTO message, Principal principal) throws YouDontHavePermissionExceptiom, IOException {
        var arrReq = message.getContent().split(" ");
        var operate = arrReq[0];
        String[] names;

        switch (operate) {
            case "help":
                return ResponseEntity.ok(List.of(new MessageSendResponseDTO(YOUTUBE_INFO)));
            case "find":
                names = arrReq[2].split("||");

                if (arrReq.length == 3) {
                    var videoName = names[0];
                    var channelName = names[1];
                    var videoId = this.youTubeOperate.findVideoIdBy(videoName, channelName);
                    if (videoId == null) return DEFAULT_BAD_RESPONSE;


                    var result = LINK_FORM + videoId;
                    return ResponseEntity.ok(List.of(new MessageSendResponseDTO(result)));

                } else if (arrReq[3].equals("-v")) {
                    var videoName = names[0];
                    var channelName = names[1];
                    var videoId = this.youTubeOperate.findVideoIdBy(videoName, channelName);
                    if (videoId == null) return DEFAULT_BAD_RESPONSE;

                    var viewCount = this.youTubeOperate.getViewsBy(videoId);

                    var result = LINK_FORM + videoId + "\n" + viewCount;
                    return ResponseEntity.ok(List.of(new MessageSendResponseDTO(result)));

                } else if (arrReq[3].equals("-l")) {
                    var videoName = names[0];
                    var channelName = names[1];
                    var videoId = this.youTubeOperate.findVideoIdBy(videoName, channelName);
                    if (videoId == null) return DEFAULT_BAD_RESPONSE;

                    var likesCount = this.youTubeOperate.getLikesBy(videoId);

                    var result = LINK_FORM + videoId + "\n" + likesCount;
                    return ResponseEntity.ok(List.of(new MessageSendResponseDTO(result)));
                }

            case "channelInfo":
                names = arrReq[2].split("||");
                var channelName = names[0];
                var channelId = this.youTubeOperate.findChannelIdInYouTube(channelName);
                if (channelId == null) return DEFAULT_BAD_RESPONSE;

                var lastVidPlaylistId = this.youTubeOperate.findPlaylistId(channelName);
                var idStr = new StringBuilder();
                this.youTubeOperate.findArrIdBy(lastVidPlaylistId).stream()
                        .forEach(id -> idStr.append(LINK_FORM + id + "\n"));

                return ResponseEntity.ok(List.of(new MessageSendResponseDTO(channelName), new MessageSendResponseDTO(idStr.toString())));

            case "videoCommentRandom":
                names = arrReq[2].split("||");
                var nameVideo = names[0];
                var nameChannel = names[1];
                var idVideo = this.youTubeOperate.findVideoIdBy(nameVideo, nameChannel);
                if (idVideo == null) return DEFAULT_BAD_RESPONSE;

                var comments = this.youTubeOperate.getComment(idVideo).stream()
                        .map(msg -> new MessageSendResponseDTO(msg))
                        .collect(Collectors.toList());
                return ResponseEntity.ok(comments);

            default:
                return ResponseEntity.ok(List.of(new MessageSendResponseDTO("Invalid operation")));
        }
    }
}
