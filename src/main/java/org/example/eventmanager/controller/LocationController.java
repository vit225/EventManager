package org.example.eventmanager.controller;

import jakarta.validation.Valid;
import org.example.eventmanager.domain.Location;
import org.example.eventmanager.converter.LocationDtoConverter;
import org.example.eventmanager.service.LocationService;
import org.example.eventmanager.dto.LocationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;
    private final LocationDtoConverter converter;
    private static final Logger log = LoggerFactory.getLogger(LocationController.class);

    public LocationController(LocationService locationService, LocationDtoConverter converter) {
        this.locationService = locationService;
        this.converter = converter;
    }

    @GetMapping
    public List<LocationDto> getAllLocations() {
        log.info("Get request for get all locations");
        return locationService.getAllLocations()
                .stream()
                .map(converter::toDto)
                .toList();
    }

    @PostMapping
    public ResponseEntity<LocationDto> createLocation(@RequestBody @Valid LocationDto locationToCreate) {
        log.info("Get request for create location: location={}", locationToCreate);
        Location createdLocation = locationService.createLocation(converter.toDomain(locationToCreate));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(converter.toDto(createdLocation));
    }

    @DeleteMapping("/{locationId}")
    public ResponseEntity<Void> deleteLocationById(@PathVariable Integer locationId) {
        log.info("Get request for delete location: id={}", locationId);
        locationService.deleteLocation(locationId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/{locationId}")
    public ResponseEntity<LocationDto> getLocationById(@PathVariable Integer locationId) {
        log.info("Get request for get location by id: id={}", locationId);
        Location foundLocation = locationService.getLocationById(locationId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(converter.toDto(foundLocation));
    }

    @PutMapping("/{locationId}")
    public ResponseEntity<LocationDto> updateLocation(
            @PathVariable Integer locationId,
            @RequestBody @Valid LocationDto locationDtoToUpdate
    ) {
        log.info("Get request for update location: id={}, locationToUpdate={}", locationId, locationDtoToUpdate);
        Location updateLocation = locationService.updateLocation(locationId, converter.toDomain(locationDtoToUpdate));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(converter.toDto(updateLocation));
    }
}
