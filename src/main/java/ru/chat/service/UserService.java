package ru.chat.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.chat.dto.userDTO.RegUserDTO;
import ru.chat.dto.userDTO.UserUpdateDTO;
import ru.chat.entity.User;
import ru.chat.entity.enums.AppRole;
import ru.chat.mapper.UserMapper;
import ru.chat.repository.UserInChatRepository;
import ru.chat.repository.UserRepository;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.List;

@Slf4j
@Component
@Transactional
@AllArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final UserInChatRepository userInChatRepository;

    private User fromPrincipal(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(""));
    }

    public User create(RegUserDTO userDTO) {
        User user = userRepository.save(userMapper.create(userDTO));
        log.info("{} was created", user);
        return user;
    }

    public User getById(Long id) throws UsernameNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with id - %d not found exception", id)));
        log.info("Get user - {} with id - {}", user, id);
        return user;
    }

    public List<User> getAll() {
        log.info("Get all users");
        return userRepository.findAll();
    }

    // * удаление других пользователей для админов приложения
    public void delete(Long id, Principal principal) throws YouDontHavePermissionExceptiom {
        User admin = this.fromPrincipal(principal);

        if (admin.getRole() == AppRole.ROLE_ADMIN) {
            User user = userRepository.getById(id);
            userRepository.delete(user);
            userInChatRepository.deleteAllByUser(user);
            log.info("User with id - {} was deleted", id);
        } else {
            throw new YouDontHavePermissionExceptiom("Only admin can delete other user from app");
        }
    }

    // * удаление себя
    public void delete(Principal principal) {
        User user = this.fromPrincipal(principal);
        userRepository.delete(user);

        log.info("Current user was deleted (username - {})", user.getUsername());
    }

    public UserUpdateDTO update(UserUpdateDTO userDTO, Principal principal) {
        User user = fromPrincipal(principal);

        user.setEmail(userDTO.getEmail());
        user.setUsername(userDTO.getUsername());

        userRepository.save(user);

        log.info("{} was updated", userDTO.getUsername());
        return userDTO;
    }

    public Object getCurrent(Principal principal) {
        User user = this.fromPrincipal(principal);

        log.info("{} go into profile", user.getUsername());
        return userMapper.toUserResponseDTO(user);
    }
}
