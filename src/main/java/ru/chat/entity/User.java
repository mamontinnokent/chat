package ru.chat.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.chat.entity.enums.AppRole;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true)
    private String username;

    @Email
    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    private boolean blocked;

    private AppRole role = AppRole.ROLE_USER;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<UserInChat> chats = new HashSet<>();
}
