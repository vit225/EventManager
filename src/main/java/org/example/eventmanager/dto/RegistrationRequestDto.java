package org.example.eventmanager.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class RegistrationRequestDto {

    @NotNull
    @NotEmpty
    private String login;

    @NotNull
    @NotEmpty
    private String password;

    @NotNull
    private Integer age;

    public RegistrationRequestDto(String login, Integer age, String password) {
        this.login = login;
        this.age = age;
        this.password = password;
    }

    public @NotNull @NotEmpty String getLogin() {
        return login;
    }

    public void setLogin(@NotNull @NotEmpty String login) {
        this.login = login;
    }

    public @NotNull @NotEmpty String getPassword() {
        return password;
    }

    public void setPassword(@NotNull @NotEmpty String password) {
        this.password = password;
    }

    public @NotNull Integer getAge() {
        return age;
    }

    public void setAge(@NotNull Integer age) {
        this.age = age;
    }
}
