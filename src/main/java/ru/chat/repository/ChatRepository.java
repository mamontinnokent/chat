package ru.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.chat.entity.Chat;

import java.util.List;


@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> getAllByPrivacy(boolean privacy);
}
