package org.example.eventmanager.converter;

import org.example.eventmanager.domain.Location;
import org.example.eventmanager.dto.LocationDto;
import org.springframework.stereotype.Component;

@Component
public class LocationDtoConverter {

    public LocationDto toDto(Location location) {
        return new LocationDto(
                location.getId(),
                location.getName(),
                location.getAddress(),
                location.getCapacity(),
                location.getDescription());
    }

    public Location toDomain(LocationDto locationDto) {
        return new Location(
                locationDto.getId(),
                locationDto.getName(),
                locationDto.getAddress(),
                locationDto.getCapacity(),
                locationDto.getDescription());
    }
}
