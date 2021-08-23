package ru.chat.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.chat.entity.enums.ChatRole;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Table
@Entity
@Getter
@Setter
public class UserInChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private ChatRole role;

    private boolean inChat;

    private Timestamp blockedTime;

    private Timestamp kickedTime;

    @ManyToOne(cascade = CascadeType.REFRESH)
    private User user;

    @ManyToOne(cascade = CascadeType.REFRESH)
    private Chat chat;

    @OneToMany(mappedBy = "owner")
    private List<Message> messages = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        kickedTime = Timestamp.valueOf(LocalDateTime.now());
        blockedTime = Timestamp.valueOf(LocalDateTime.now());
    }
}
