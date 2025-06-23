package org.example.eventmanager.converter;

import org.example.eventmanager.domain.Role;
import org.example.eventmanager.domain.User;
import org.example.eventmanager.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserEntityConverter {

    public User toDomain(UserEntity userEntity) {
        return new User(
                userEntity.getId(),
                userEntity.getLogin(),
                userEntity.getPasswordHash(),
                userEntity.getAge(),
                Role.valueOf(userEntity.getRole())
        );
    }
}
