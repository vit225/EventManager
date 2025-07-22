package org.example.eventmanager.controller;

import jakarta.validation.Valid;
import org.example.eventmanager.security.jwt.JwtTokenResponse;
import org.example.eventmanager.dto.AuthRequestDto;
import org.example.eventmanager.converter.UserDtoConverter;
import org.example.eventmanager.domain.User;
import org.example.eventmanager.dto.RegistrationRequestDto;
import org.example.eventmanager.dto.UserDto;
import org.example.eventmanager.security.jwt.AuthenticationService;
import org.example.eventmanager.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserDtoConverter userDtoConverter;
    private final AuthenticationService authenticationService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, UserDtoConverter userDtoConverter, AuthenticationService authenticationService) {
        this.userService = userService;
        this.userDtoConverter = userDtoConverter;
        this.authenticationService = authenticationService;
    }

    @PostMapping()
    public UserDto registration(@RequestBody @Valid RegistrationRequestDto requestDto) {
        User user = userService.registration(requestDto);
        return userDtoConverter.toDto(user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable Integer userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDtoConverter.toDto(user));
    }

    @PostMapping("/auth")
    public ResponseEntity<JwtTokenResponse> authenticate(@RequestBody @Valid AuthRequestDto requestDto) {
        String login = requestDto.getLogin();
        log.info("Get request for sign-in: login={}", login);
        User user = userService.findByLogin(login);
        String userId = String.valueOf(user.getId());
        String token = authenticationService.authenticateUser(requestDto, userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new JwtTokenResponse(token));
    }
}
