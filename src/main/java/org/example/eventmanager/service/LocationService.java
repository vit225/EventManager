package org.example.eventmanager.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.eventmanager.domain.Location;
import org.example.eventmanager.entity.LocationEntity;
import org.example.eventmanager.converter.LocationEntityConverter;
import org.example.eventmanager.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final LocationEntityConverter converter;

    private final LocationRepository locationRepository;

    public LocationService(LocationEntityConverter converter, LocationRepository locationRepository) {
        this.converter = converter;
        this.locationRepository = locationRepository;
    }

    public List<Location> getAllLocations() {
        return locationRepository.findAll()
                .stream()
                .map(converter::toDomain)
                .toList();
    }

    public Location createLocation(Location location) {
        LocationEntity savedLocationEntity = locationRepository.save(converter.toEntity(location));
        return converter.toDomain(savedLocationEntity);
    }

    public void deleteLocation(Integer locationId) {
        if (!locationRepository.existsById(locationId)) {
            throw new EntityNotFoundException("Не найдена локация по идентификатору=%s".formatted(locationId));
        }

        locationRepository.deleteById(locationId);
    }

    public Location getLocationById(Integer locationId) {
        LocationEntity locationEntity = locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Не найдена локация по идентификатору=%s".formatted(locationId)));
        return converter.toDomain(locationEntity);
    }

    public Location updateLocation(Integer locationId, Location locationToUpdate) {
        if (!locationRepository.existsById(locationId)) {
            throw new EntityNotFoundException("Не найдена локация по идентификатору=%s".formatted(locationId));
        }

        locationToUpdate.setId(locationId);
        LocationEntity updatedLocation = locationRepository.save(converter.toEntity(locationToUpdate));

        return converter.toDomain(updatedLocation);
    }
}
