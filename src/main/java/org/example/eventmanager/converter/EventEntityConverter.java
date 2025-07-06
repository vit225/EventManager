package org.example.eventmanager.converter;

import org.example.eventmanager.domain.Event;
import org.example.eventmanager.domain.Registration;
import org.example.eventmanager.entity.EventEntity;
import org.springframework.stereotype.Component;

@Component
public class EventEntityConverter {

    public Event toDomain(EventEntity eventEntity) {
        return new Event(
                eventEntity.getId(),
                eventEntity.getName(),
                eventEntity.getOwnerId(),
                eventEntity.getDate(),
                eventEntity.getRegistrationList().stream()
                        .map(registrationEntity -> new Registration(
                                registrationEntity.getId(),
                                registrationEntity.getUserId(),
                                eventEntity.getId()
                        ))
                        .toList(),
                eventEntity.getLocationId(),
                eventEntity.getStatus(),
                eventEntity.getCost(),
                eventEntity.getDuration(),
                eventEntity.getMaxPlaces()
        );
    }
}
