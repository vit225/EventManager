package org.example.eventmanager.converter;

import org.example.eventmanager.domain.User;
import org.example.eventmanager.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserDtoConverter {

    public UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getLogin(),
                user.getAge(),
                user.getRole().name()
        );
    }
}
