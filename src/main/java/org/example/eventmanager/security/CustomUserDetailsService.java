package org.example.eventmanager.security;

import org.example.eventmanager.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        return userRepository.findByLogin(username)
                .map(userEntity -> new User(
                        userEntity.getLogin(),
                        userEntity.getPasswordHash(),
                        List.of(new SimpleGrantedAuthority(userEntity.getRole()))
                ))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with user login: = %s"
                        .formatted(username)));
    }
}