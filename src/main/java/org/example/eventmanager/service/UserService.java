package org.example.eventmanager.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.eventmanager.converter.UserEntityConverter;
import org.example.eventmanager.domain.Role;
import org.example.eventmanager.domain.User;
import org.example.eventmanager.dto.RegistrationRequestDto;
import org.example.eventmanager.entity.UserEntity;
import org.example.eventmanager.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEntityConverter userEntityConverter;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserEntityConverter userEntityConverter) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userEntityConverter = userEntityConverter;
    }

    public User registration(RegistrationRequestDto requestDto) {
        if (userRepository.existsByLogin(requestDto.getLogin())) {
            throw new IllegalArgumentException("Имя пользователя уже занято");
        }
        String passwordHash = passwordEncoder.encode(requestDto.getPassword());
        UserEntity userToSave = new UserEntity(
                null,
                requestDto.getLogin(),
                passwordHash,
                requestDto.getAge(),
                Role.USER.name()
        );
        UserEntity savedUser = userRepository.save(userToSave);

        return userEntityConverter.toDomain(savedUser);
    }

    public User getUserById(Integer userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("No found user by id=%s".formatted(userId)));
        return userEntityConverter.toDomain(userEntity);
    }

    public User findByLogin(String login) {
        var user = userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return userEntityConverter.toDomain(user);
    }

    public void createDefaultUsers() {
        if (!userRepository.existsByLogin("admin")) {
            UserEntity admin = new UserEntity();
            admin.setLogin("admin");
            admin.setPasswordHash(passwordEncoder.encode("admin"));
            admin.setRole(Role.ADMIN.name());
            userRepository.save(admin);
        }
        if (!userRepository.existsByLogin("user")) {
            UserEntity user = new UserEntity();
            user.setLogin("user");
            user.setPasswordHash(passwordEncoder.encode("user"));
            user.setRole(Role.USER.name());
            userRepository.save(user);
        }
    }
}
