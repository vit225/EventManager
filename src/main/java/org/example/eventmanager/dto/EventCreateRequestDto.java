package org.example.eventmanager.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class EventCreateRequestDto {

    @NotBlank(message = "Название мероприятия обязательно")
    private String name;

    @NotNull(message = "Максимальное количество мест обязательно")
    @Min(value = 1, message = "Минимальное количество мест должно быть равно 1")
    private Integer maxPlaces;

    @NotNull(message = "Дата и время проведения обязательны")
    @Future(message = "Дата и время должны быть в будущем")
    private LocalDateTime date;

    @NotNull(message = "Стоимость обязательна")
    @Min(value = 0, message = "Стоимость должна быть не меньше 0")
    private Integer cost;

    @NotNull(message = "Длительность обязательна")
    @Min(value = 30, message = "Длительность минимум 30 минут")
    private Integer duration;

    @NotNull(message = "ID локации обязателен")
    private Integer locationId;

    public EventCreateRequestDto(String name, Integer maxPlaces, LocalDateTime date, Integer cost,
                                 Integer duration, Integer locationId) {
        this.name = name;
        this.maxPlaces = maxPlaces;
        this.date = date;
        this.cost = cost;
        this.duration = duration;
        this.locationId = locationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getMaxPlaces() {
        return maxPlaces;
    }

    public void setMaxPlaces(Integer maxPlaces) {
        this.maxPlaces = maxPlaces;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }
}
