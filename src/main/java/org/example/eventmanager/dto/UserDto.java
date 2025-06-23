package org.example.eventmanager.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class UserDto {

    @NotNull
    private Integer id;

    @NotNull
    @NotEmpty
    private String login;

    @NotNull
    private Integer age;

    @NotNull
    @NotEmpty
    private String role;

    public UserDto(Integer id, String login, Integer age, String role) {
        this.id = id;
        this.login = login;
        this.age = age;
        this.role = role;
    }

    public @NotNull Integer getId() {
        return id;
    }

    public void setId(@NotNull Integer id) {
        this.id = id;
    }

    public @NotNull @NotEmpty String getLogin() {
        return login;
    }

    public void setLogin(@NotNull @NotEmpty String login) {
        this.login = login;
    }

    public @NotNull Integer getAge() {
        return age;
    }

    public void setAge(@NotNull Integer age) {
        this.age = age;
    }

    public @NotNull @NotEmpty String getRole() {
        return role;
    }

    public void setRole(@NotNull @NotEmpty String role) {
        this.role = role;
    }
}
