package org.example.eventmanager.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public class LocationDto {

    @Null()
    private Integer id;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private String address;

    @NotNull
    @Min(5)
    private Integer capacity;

    private String description;

    public LocationDto(Integer id, String name, String address, Integer capacity, String description) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public @NotNull @NotEmpty String getName() {
        return name;
    }

    public void setName(@NotNull @NotEmpty String name) {
        this.name = name;
    }

    public @NotNull @NotEmpty String getAddress() {
        return address;
    }

    public void setAddress(@NotNull @NotEmpty String address) {
        this.address = address;
    }

    public @NotNull @Min(5) Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(@NotNull @Min(5) Integer capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
