package ru.chat.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "massage")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String content;

    private Timestamp creationDate;

    @ManyToOne(cascade = CascadeType.REFRESH)
    private Chat chat;

    @ManyToOne
    private UserInChat owner;

    public Message(String username, String content, Chat chat, UserInChat owner) {
        this.username = username;
        this.content = content;
        this.chat = chat;
        this.owner = owner;
    }

    @PrePersist
    protected void onCreated() {
        this.creationDate = Timestamp.valueOf(LocalDateTime.now());
    }

}
