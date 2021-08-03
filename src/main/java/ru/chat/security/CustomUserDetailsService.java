package ru.chat.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.chat.repository.UserRepository;


@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User doesn't exists"));
        return CustomUserDetails.fromUser(user);
    }

    public CustomUserDetails loadUserById(Long id) throws UsernameNotFoundException {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User doesn't exists"));
        return CustomUserDetails.fromUser(user);
    }
}
