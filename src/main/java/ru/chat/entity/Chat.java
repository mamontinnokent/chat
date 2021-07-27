package ru.chat.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "chat")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameChat;

    private String caption;

    private Timestamp creationDate;

    @OneToMany(
            mappedBy = "chat",
            cascade = {CascadeType.REFRESH, CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST},
            fetch = FetchType.EAGER
    )
    private List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "chat")
    private Set<UserInChat> members = new HashSet<>();

    public Chat(String nameChat, String caption) {
        this.nameChat = nameChat;
        this.caption = caption;
    }

    @PrePersist
    protected void onCreate() {
        this.creationDate = Timestamp.valueOf(LocalDateTime.now());
    }
}
