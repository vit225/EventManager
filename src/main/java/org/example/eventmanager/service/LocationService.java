package org.example.eventmanager.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.eventmanager.domain.Location;
import org.example.eventmanager.entity.LocationEntity;
import org.example.eventmanager.converter.LocationEntityConverter;
import org.example.eventmanager.repository.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Location createLocation(Location location) {
        LocationEntity savedLocationEntity = locationRepository.save(converter.toEntity(location));
        return converter.toDomain(savedLocationEntity);
    }

    @Transactional
    public void deleteLocation(Integer locationId) {
        if (!locationRepository.existsById(locationId)) {
            throw new EntityNotFoundException("Not found location by id=%s".formatted(locationId));
        }

        locationRepository.deleteById(locationId);
    }

    public Location getLocationById(Integer locationId) {
        LocationEntity locationEntity = locationRepository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("No found location by id=%s".formatted(locationId)));
        return converter.toDomain(locationEntity);
    }

    @Transactional
    public Location updateLocation(Integer locationId, Location locationToUpdate) {
        if (!locationRepository.existsById(locationId)) {
            throw new EntityNotFoundException("No found location by id=%s".formatted(locationId));
        }

        locationRepository.updateLocation(
                locationId,
                locationToUpdate.getName(),
                locationToUpdate.getAddress(),
                locationToUpdate.getCapacity(),
                locationToUpdate.getDescription());

        return converter.toDomain(locationRepository.findById(locationId).orElseThrow());
    }
}
