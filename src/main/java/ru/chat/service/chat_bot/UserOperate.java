package ru.chat.service.chat_bot;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.chat.entity.User;
import ru.chat.repository.UserRepository;

import java.security.Principal;

@Component
@AllArgsConstructor
public class UserOperate {

    private final UserRepository userRepository;

    //    * Получаем текущего пользователя, утилитарный метод
    private User fromPrincipal(Principal principal) {
        return this.userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public void rename(String currentName, String newName, Principal principal) {
        User user = this.userRepository.findByUsername(currentName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        User principalUser = this.fromPrincipal(principal);

        if (user.getUsername() == principalUser.getUsername() || principalUser.+)
    }
}
