package ru.chat.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.chat.dto.userDTO.RegUserDTO;
import ru.chat.dto.userDTO.UserResponseDTO;
import ru.chat.dto.userDTO.UserUpdateDTO;
import ru.chat.entity.User;
import ru.chat.entity.enums.AppRole;
import ru.chat.mapper.UserMapper;
import ru.chat.repository.UserInChatRepository;
import ru.chat.repository.UserRepository;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
@AllArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final UserInChatRepository userInChatRepository;

    // * Получаем текущего пользователя, утилитарный метод
    private User fromPrincipal(Principal principal) {
        return this.userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(""));
    }

    public User create(RegUserDTO userDTO) throws Exception {
            User user = this.userRepository.save(userMapper.create(userDTO));
            log.info("{} был создан", user.getUsername());
            return user;
    }

    public UserResponseDTO getById(Long id) throws UsernameNotFoundException {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with id - %d not found exception", id)));
        log.info("Получен пользователь - {} с id - {}", user.getUsername(), id);
        return userMapper.toUserResponseDTO(user);
    }

    public Map<String, Long> getAll() {
        log.info("Получены все пользователи");
        return userRepository.findAll().stream()
                .collect(Collectors.toMap(k -> k.getUsername(), v -> v.getId()));
    }

    // * удаление других пользователей для админов приложения
    public void delete(Long id, Principal principal) throws YouDontHavePermissionExceptiom {
        User admin = this.fromPrincipal(principal);

        if (admin.getRole() == AppRole.ROLE_ADMIN) {
            User user = this.userRepository.getById(id);
            this.userRepository.delete(user);
            this.userInChatRepository.deleteAllByUser(user);
            log.info("Пользователь с id - {} был удален", id);
        } else {
            throw new YouDontHavePermissionExceptiom("Only admin can delete other user from app");
        }
    }

    // * удаление себя
    public void delete(Principal principal) {
        User user = this.fromPrincipal(principal);
        this.userRepository.delete(user);

        log.info("Текущий пользователь был удалён (username - {})", user.getUsername());
    }

    public UserUpdateDTO update(UserUpdateDTO userDTO, Principal principal) {
        User user = this.fromPrincipal(principal);

        user.setEmail(userDTO.getEmail());
        user.setUsername(userDTO.getUsername());

        this.userRepository.save(user);

        log.info("{} был обновлён", userDTO.getUsername());
        return userDTO;
    }

    public UserResponseDTO getCurrent(Principal principal) {
        User user = this.fromPrincipal(principal);

        log.info("{} зашёл в свой профиль", user.getUsername());
        return this.userMapper.toUserResponseDTO(user);
    }
}
