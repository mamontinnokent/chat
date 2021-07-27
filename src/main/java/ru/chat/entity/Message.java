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

    private String content;

    private Timestamp creationDate;

    @ManyToOne(cascade = CascadeType.REFRESH)
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "owner_id")
<<<<<<< Updated upstream
    private User owner;
=======
    private UserInChat owner;
>>>>>>> Stashed changes

    @PrePersist
    protected void onCreated() {
        this.creationDate = Timestamp.valueOf(LocalDateTime.now());
    }

}
