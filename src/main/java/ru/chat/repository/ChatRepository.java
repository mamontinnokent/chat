package ru.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.chat.entity.Chat;

import java.util.List;
import java.util.Optional;


@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> findByNameChat(String nameChat);
    Chat getByNameChat(String chatName);
    List<Chat> getAllByPrivacy(boolean privacy);
}
