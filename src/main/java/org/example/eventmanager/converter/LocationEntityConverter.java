package org.example.eventmanager.converter;

import org.example.eventmanager.domain.Location;
import org.example.eventmanager.entity.LocationEntity;
import org.springframework.stereotype.Component;

@Component
public class LocationEntityConverter {

    public Location toDomain(LocationEntity locationEntity) {
        return new Location(locationEntity.getId(),
                locationEntity.getName(),
                locationEntity.getAddress(),
                locationEntity.getCapacity(),
                locationEntity.getDescription());
    }

    public LocationEntity toEntity(Location location) {
        return new LocationEntity(location.getId(),
                location.getName(),
                location.getAddress(),
                location.getCapacity(),
                location.getDescription());
    }
}
