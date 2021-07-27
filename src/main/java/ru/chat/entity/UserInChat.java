package ru.chat.entity;


import lombok.Getter;
import lombok.Setter;
import ru.chat.entity.enums.ChatRole;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table
public class UserInChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private ChatRole role;

    private boolean blocked;

    @ManyToOne
    private User user;

    @ManyToOne
    private Chat chat;

    @OneToMany(mappedBy = "owner")
    private List<Message> messages = new ArrayList<>();
}
