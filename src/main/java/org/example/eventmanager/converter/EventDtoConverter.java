package org.example.eventmanager.converter;

import org.example.eventmanager.domain.Event;
import org.example.eventmanager.dto.EventDto;
import org.springframework.stereotype.Component;

@Component
public class EventDtoConverter {
    public EventDto toDto(Event event) {
        return new EventDto(
                event.getId(), event.getName(), event.getOwnerId(),
                event.getMaxPlaces(), event.getRegistrationList().size(), event.getDate(), event.getCost(),
                event.getDuration(), event.getLocationId(), event.getStatus()
        );
    }

}
