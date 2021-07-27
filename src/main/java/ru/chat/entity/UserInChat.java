package ru.chat.entity;


import lombok.Getter;
import lombok.Setter;
import ru.chat.entity.enums.ChatRole;

import javax.persistence.*;
<<<<<<< Updated upstream
=======
import java.util.List;
>>>>>>> Stashed changes

@Entity
@Getter
@Setter
@Table
public class UserInChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

<<<<<<< Updated upstream
=======
    private String username;

>>>>>>> Stashed changes
    private ChatRole role;

    private boolean isBlocked;

    @ManyToOne
    private User user;

    @ManyToOne
    private Chat chat;
<<<<<<< Updated upstream
=======

    @OneToMany(mappedBy = "owner")
    private List<Message> messages;
>>>>>>> Stashed changes
}
