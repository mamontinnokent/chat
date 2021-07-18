package ru.chat.service;

import ru.chat.entity.User;

import java.util.List;

public interface UserService {

    User save(User user);
    User getById(Long id);
    List<User> findAll();
    void deleteById(Long id);

}
