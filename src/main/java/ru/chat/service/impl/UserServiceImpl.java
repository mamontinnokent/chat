package ru.chat.service.impl;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.chat.entity.User;
import ru.chat.repository.UserRepository;
import ru.chat.service.UserService;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;

    public User save(UserDTO userDTO) {
        // ! some code
        return userRepo.save();
    }

    public User getById(Long id) {
        return userRepo.getById(id);
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public User update(UserDTO userDTO) {
        User user = new User();
        // ! some code
        return user;
    }

    public void deleteById(Long id) {
        User user = userRepo.getById(id);
        userRepo.delete(user);
    }
}
