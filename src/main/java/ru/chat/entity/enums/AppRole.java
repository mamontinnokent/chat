package ru.chat.entity.enums;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum AppRole {
    ROLE_USER,
    ROLE_ADMIN;

    public List<SimpleGrantedAuthority> getAuthorities() {
        return Stream.of(this.name())
                .map(s -> new SimpleGrantedAuthority(s))
                .collect(Collectors.toList());
    }
}