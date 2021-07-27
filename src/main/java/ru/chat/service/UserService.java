package ru.chat.service;

<<<<<<< Updated upstream
import ru.chat.entity.User;

import java.util.List;

public interface UserService {

    User save(User user);
    User getById(Long id);
    List<User> findAll();
    void deleteById(Long id);

=======

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.chat.dto.userDTO.RegUserDTO;
import ru.chat.dto.userDTO.UpdateUserDTO;
import ru.chat.dto.userDTO.UserResponseDTO;
import ru.chat.entity.User;
import ru.chat.mapper.UserMapper;
import ru.chat.repository.UserRepository;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Transactional
    public void register(RegUserDTO requestDTO) {
        User user = userRepository.save(userMapper.create(requestDTO));
        log.info("User was created {}", user);
    }

    public UserResponseDTO getById(Long id) {
        User user = userRepository.getById(id);
        log.info("Get user - {}, with id - {}", user, id);
        UserResponseDTO dto = userMapper.toUserResponseDTO(user);
        return dto;
    }

    private UserResponseDTO getCurrent(Principal principal) {
        log.info("Get current user ");
        return userMapper.toUserResponseDTO(this.getFromPrincipal(principal));
    }

    @Transactional
    public void deleteCurrent(Principal principal) {
        User user = this.getFromPrincipal(principal);
        userRepository.delete(user);
        log.info("User - {} was deleted", user);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Transactional
    public UpdateUserDTO update(UpdateUserDTO userDTO, Principal principal) {
        User user = this.getFromPrincipal(principal);
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        userRepository.save(user);
        log.info("User - {} was updated", user);
        return userDTO;
    }

    @Transactional
    public void deleteById(Long id) {
        User user = userRepository.getById(id);
        userRepository.delete(user);
        log.info("User with id - {} was deleted", id);
    }

    private User getFromPrincipal(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(""));
    }
>>>>>>> Stashed changes
}
