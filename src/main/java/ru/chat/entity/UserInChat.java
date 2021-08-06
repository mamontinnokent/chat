package ru.chat.entity;

import lombok.Getter;
import lombok.Setter;
import ru.chat.entity.enums.ChatRole;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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

    private boolean inChat;

    private boolean kicked;

    private Timestamp dateAfterGoIn;

    @ManyToOne(cascade = CascadeType.REFRESH)
    private User user;

    @ManyToOne(cascade = CascadeType.REFRESH)
    private Chat chat;

    @OneToMany(mappedBy = "owner")
    private List<Message> messages = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dateAfterGoIn = Timestamp.valueOf(LocalDateTime.now());
    }
}
