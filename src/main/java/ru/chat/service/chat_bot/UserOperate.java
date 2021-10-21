package ru.chat.service.chat_bot;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.chat.entity.User;
import ru.chat.entity.enums.AppRole;
import ru.chat.entity.enums.ChatRole;
import ru.chat.repository.ChatRepository;
import ru.chat.repository.UserInChatRepository;
import ru.chat.repository.UserRepository;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;

import javax.transaction.Transactional;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Slf4j
@Component
@Transactional
@AllArgsConstructor
public class UserOperate {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final UserInChatRepository userInChatRepository;

    //      * Получаем текущего пользователя, утилитарный метод
    private User fromPrincipal(Principal principal) {
        return this.userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public void rename(String currentName, String newName, Principal principal) {
        User user = this.userRepository.findByUsername(currentName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        User principalUser = this.fromPrincipal(principal);

        if (user.getUsername() == principalUser.getUsername() || principalUser.getRole() == AppRole.ROLE_ADMIN) {
            user.setUsername(newName);
            userRepository.save(user);
            log.info("Пользователь с id = {} поменял имя.", user.getId());
        }
    }

    public void setModerator(String username, String chatName, Principal principal, boolean doYouModerator) throws YouDontHavePermissionExceptiom {
        var user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
        var chat = this.chatRepository.getByNameChat(chatName);
        var moderatorInFuture = this.userInChatRepository.findByUserAndChat(user, chat).orElse(null);

        if (moderatorInFuture.getRole() == ChatRole.ROLE_CREATOR
                || moderatorInFuture.getRole() == ChatRole.ROLE_ADMIN)
            return;

        var admin = this.userInChatRepository.findByUserAndChat(this.fromPrincipal(principal), chat).orElse(null);

        if ((admin.getRole() == ChatRole.ROLE_ADMIN || moderatorInFuture.getRole() == ChatRole.ROLE_USER) && !doYouModerator) {
            moderatorInFuture.setRole(ChatRole.ROLE_MODERATOR);

            this.userInChatRepository.save(moderatorInFuture);
            log.info("{} назначен модератором.", user.getUsername());
        } else if ((admin.getRole() == ChatRole.ROLE_ADMIN || moderatorInFuture.getRole() == ChatRole.ROLE_MODERATOR) && doYouModerator) {
            moderatorInFuture.setRole(ChatRole.ROLE_USER);

            this.userInChatRepository.save(moderatorInFuture);
            log.info("{} понижен до юзера.", user.getUsername());
        } else {
            throw new YouDontHavePermissionExceptiom("You don't have permission");
        }
    }

    public void ban(Principal principal, String username) {
        var admin = this.fromPrincipal(principal);

        if (admin.getRole() == AppRole.ROLE_ADMIN) {
            var user = this.userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found exception"));
            this.userInChatRepository.findAllByUserAndInChat(user, true)
                    .forEach(t -> {
                        t.setInChat(false);
                        t.setKickedTime(Timestamp.valueOf(LocalDateTime.now().plusYears(1)));
                    });

            log.info("{} был забанен.", user.getUsername());
        }
    }
    public void ban(Principal principal, String username, Long minutes) {
        var admin = this.fromPrincipal(principal);

        if (admin.getRole() == AppRole.ROLE_ADMIN) {
            var user = this.userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found exception"));
            this.userInChatRepository.findAllByUserAndInChat(user, true)
                    .forEach(t -> {
                        t.setInChat(false);
                        t.setKickedTime(Timestamp.valueOf(LocalDateTime.now().plusMinutes(minutes)));
                    });

            log.info("{} был забанен на {} минут.", user.getUsername(), minutes);
        }
    }
}
