package ru.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.chat.entity.Chat;
import ru.chat.entity.User;
import ru.chat.entity.UserInChat;

import java.util.List;

@Repository
public interface UserInChatRepository extends JpaRepository<UserInChat, Long> {
    List<UserInChat> findAllByChatAndAndInChat(Chat chat, boolean inChat);
    List<UserInChat> findAllByUserAndInChat(User user, Boolean inChat);

    UserInChat findByUserAndChat(User user, Chat chat);
    void deleteAllByUser(User user);
}
