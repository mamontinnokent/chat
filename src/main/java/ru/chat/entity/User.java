package ru.chat.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.chat.entity.enums.AppRole;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    private boolean blocked;

    private AppRole role = AppRole.ROLE_USER;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserInChat> chats;
}
