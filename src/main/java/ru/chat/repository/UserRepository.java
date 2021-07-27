package ru.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.chat.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
<<<<<<< Updated upstream
    User findByEmail(String email);
=======
   Optional<User> findByEmail(String email);
>>>>>>> Stashed changes
}
