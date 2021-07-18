package ru.chat.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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

    private Timestamp creationDate;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "user_id")
    private User creator;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Message> messages;

    @OneToMany(mappedBy = "chat")
    private Set<UserInChat> members;


    @PrePersist
    protected void onCreate() {
        this.creationDate = Timestamp.valueOf(LocalDateTime.now());
    }
}
