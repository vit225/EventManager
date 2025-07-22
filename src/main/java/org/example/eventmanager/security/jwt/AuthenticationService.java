package org.example.eventmanager.security.jwt;

import org.example.eventmanager.domain.User;
import org.example.eventmanager.dto.AuthRequestDto;
import org.example.eventmanager.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenManager jwtTokenManager;

    private final UserService userService;


    public AuthenticationService(AuthenticationManager authenticationManager, JwtTokenManager jwtTokenManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenManager = jwtTokenManager;
        this.userService = userService;
    }

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("Authentication not present");
        }
        return (User) authentication.getPrincipal();
    }

    public String authenticateUser(AuthRequestDto requestDto, String userId) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.getLogin(),
                        requestDto.getPassword()
                )
        );
        User userById = userService.getUserById(Integer.parseInt(userId));
        return jwtTokenManager.createJwtToken(userById);
    }
}