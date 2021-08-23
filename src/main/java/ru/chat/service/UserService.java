package ru.chat.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.chat.dto.request.UserRegRequestDTO;
import ru.chat.dto.request.UserUpdateRequestDTO;
import ru.chat.dto.response.UserResponseDTO;
import ru.chat.entity.User;
import ru.chat.entity.UserInChat;
import ru.chat.entity.enums.AppRole;
import ru.chat.mapper.UserMapper;
import ru.chat.repository.UserInChatRepository;
import ru.chat.repository.UserRepository;
import ru.chat.service.exception.YouDontHavePermissionExceptiom;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final UserInChatRepository userInChatRepository;

    // * Получаем текущего пользователя, утилитарный метод
    private User fromPrincipal(Principal principal) {
        return this.userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User create(UserRegRequestDTO userDTO) throws Exception {
        var user = this.userRepository.save(userMapper.create(userDTO));
        log.info("{} был создан", user.getUsername());
        return user;
    }

    public User createAdmin(UserRegRequestDTO userDTO) throws Exception {
        var user = this.userRepository.save(userMapper.createAdmin(userDTO));
        log.info("{} был создан", user.getUsername());
        return user;
    }

    public UserResponseDTO getById(Long id) throws UsernameNotFoundException {
        var user = this.userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with id - %d not found exception", id)));
        List<UserInChat> list = this.userInChatRepository.findAllByUserAndInChat(user, true);
        log.info("Получен пользователь - {} с id - {}", user.getUsername(), id);
        return userMapper.toUserResponseDTO(user, list);
    }

    public Map<String, Long> getAll() {
        log.info("Получены все пользователи");
        var listUsers = this.userRepository.findAll();
        var map = new HashMap<String, Long>();
        listUsers.stream()
                .forEach(usr -> map.put(usr.getUsername(), usr.getId()));

        return map;
    }

    // * удаление других пользователей для админов приложения
    public void delete(Long id, Principal principal) throws YouDontHavePermissionExceptiom {
        var admin = this.fromPrincipal(principal);

        if (admin.getRole() == AppRole.ROLE_ADMIN) {
            var user = this.userRepository.getById(id);
            this.userRepository.delete(user);
            this.userInChatRepository.deleteAllByUser(user);
            log.info("Пользователь с id - {} был удален", id);
        } else {
            throw new YouDontHavePermissionExceptiom("Only admin can delete other user from app");
        }
    }

    // * удаление себя
    public void delete(Principal principal) {
        var user = this.fromPrincipal(principal);
        this.userRepository.delete(user);

        log.info("Текущий пользователь был удалён (username - {})", user.getUsername());
    }

    public UserUpdateRequestDTO update(UserUpdateRequestDTO userDTO, Principal principal) {
        var user = this.fromPrincipal(principal)
                .setEmail(userDTO.getEmail())
                .setUsername(userDTO.getUsername());

        var newUser = this.userRepository.save(user);

        log.info("{} был обновлён", userDTO.getUsername());
        return new UserUpdateRequestDTO(newUser.getUsername(), newUser.getEmail());
    }

    public UserResponseDTO getCurrent(Principal principal) {
        var user = this.fromPrincipal(principal);
        var list = this.userInChatRepository.findAllByUserAndInChat(user, true);

        log.info("{} зашёл в свой профиль", user.getUsername());
        return this.userMapper.toUserResponseDTO(user, list);
    }
}
