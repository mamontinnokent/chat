package ru.chat.service.chat_bot;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.chat.entity.User;
import ru.chat.entity.enums.AppRole;
import ru.chat.entity.enums.ChatRole;
import ru.chat.repository.ChatRepository;
import ru.chat.repository.UserInChatRepository;
import ru.chat.repository.UserRepository;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
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

        if (user.getUsername() == principalUser.getUsername() || principalUser.getRole() == user.getRole()) {
            user.setUsername(newName);
            userRepository.save(user);
        }
    }

    public void setModerator(String username, String chatName, Principal principal, boolean doYouModerator) throws YouDontHavePermissionExceptiom {
        var user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
        var chat = this.chatRepository.getByNameChat(chatName);
        var moderatorInFuture = this.userInChatRepository.findByUserAndChat(user, chat);
        var admin = this.userInChatRepository.findByUserAndChat(this.fromPrincipal(principal), chat);

        if ((admin.getRole() == ChatRole.ROLE_ADMIN || moderatorInFuture.getRole() == ChatRole.ROLE_USER) && !doYouModerator) {
            moderatorInFuture.setRole(ChatRole.ROLE_MODERATOR);
            this.userInChatRepository.save(moderatorInFuture);
        } else if ((admin.getRole() == ChatRole.ROLE_ADMIN || moderatorInFuture.getRole() == ChatRole.ROLE_MODERATOR) && doYouModerator) {
            moderatorInFuture.setRole(ChatRole.ROLE_USER);
            this.userInChatRepository.save(moderatorInFuture);
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
                        t.setKickedTime(Timestamp.valueOf(LocalDateTime.now().plusMinutes(45)));
                    });
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
        }
    }
}
