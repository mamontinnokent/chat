package ru.chat.entity;


import lombok.Getter;
import lombok.Setter;
import ru.chat.entity.enums.ChatRole;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table
public class UserInChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private ChatRole role;

    private boolean isBlocked;

    @ManyToOne
    private User user;

    @ManyToOne
    private Chat chat;
}
