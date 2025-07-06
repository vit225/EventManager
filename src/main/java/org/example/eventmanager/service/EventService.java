package org.example.eventmanager.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.eventmanager.converter.EventEntityConverter;
import org.example.eventmanager.domain.Event;
import org.example.eventmanager.domain.Location;
import org.example.eventmanager.domain.User;
import org.example.eventmanager.dto.EventCreateRequestDto;
import org.example.eventmanager.dto.EventSearchRequestDto;
import org.example.eventmanager.dto.EventUpdateRequestDto;
import org.example.eventmanager.entity.EventEntity;
import org.example.eventmanager.entity.EventStatus;
import org.example.eventmanager.repository.EventRepository;
import org.example.eventmanager.security.jwt.AuthenticationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventEntityConverter converter;
    private final AuthenticationService authenticationService;
    private final LocationService locationService;

    public EventService(
            EventRepository eventRepository,
            EventEntityConverter converter,
            AuthenticationService authenticationService,
            LocationService locationService
    ) {
        this.eventRepository = eventRepository;
        this.converter = converter;
        this.authenticationService = authenticationService;
        this.locationService = locationService;
    }

    public Event createEvent(EventCreateRequestDto eventCreateRequestDto) {
        Location location = locationService.getLocationById(eventCreateRequestDto.getLocationId());
        if (location.getCapacity() < eventCreateRequestDto.getMaxPlaces()) {
            throw new IllegalArgumentException(("Максимальное количество мест на мероприятии = %s, " +
                    "превышает вместительность локации = %s")
                    .formatted(eventCreateRequestDto.getMaxPlaces(), location.getCapacity()));
        }
        User currentUser = authenticationService.getCurrentUser();

        EventEntity eventEntity = new EventEntity(
                null,
                eventCreateRequestDto.getName(),
                currentUser.getId(),
                eventCreateRequestDto.getMaxPlaces(),
                eventCreateRequestDto.getDate(),
                eventCreateRequestDto.getCost(),
                eventCreateRequestDto.getDuration(),
                eventCreateRequestDto.getLocationId(),
                EventStatus.WAIT_START,
                List.of()
        );
        return converter.toDomain(eventRepository.save(eventEntity));
    }

    public void deleteEvent(Integer eventId) {
        User currentUser = authenticationService.getCurrentUser();
        Event foundEvent = findEventById(eventId);

        if (!currentUser.getId().equals(foundEvent.getOwnerId())) {
            throw new IllegalArgumentException("У текущего пользователя нет прав для отмены мероприятия");
        }

        if (foundEvent.getStatus().equals(EventStatus.CANCELLED)) {
            throw new IllegalArgumentException("Мероприятие с идентификатором %s уже было ранее отменено"
                    .formatted(eventId));
        }

        if (foundEvent.getStatus().equals(EventStatus.STARTED) || foundEvent.getStatus().equals(EventStatus.FINISHED)) {
            throw new IllegalArgumentException("Невозможно отменить событие со статусом %s"
                    .formatted(foundEvent.getStatus()));
        }

        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Мероприятие не найдено по идентификатору=%s"
                        .formatted(eventId)));
        eventEntity.setStatus(EventStatus.CANCELLED);
        eventRepository.save(eventEntity);
    }

    public Event findEventById(Integer eventId) {
        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Мероприятие не найдено по идентификатору=%s"
                        .formatted(eventId)));
        return converter.toDomain(eventEntity);
    }

    public Event updateEvent(Integer eventId, EventUpdateRequestDto eventUpdateRequestDto) {
        User currentUser = authenticationService.getCurrentUser();
        Event foundEvent = findEventById(eventId);

        if (!currentUser.getId().equals(foundEvent.getOwnerId())) {
            throw new IllegalArgumentException("У текущего пользователя нет прав для обновления мероприятия");
        }

        if (!foundEvent.getStatus().equals(EventStatus.WAIT_START)) {
            throw new IllegalArgumentException("Невозможно изменить статус события: %s"
                    .formatted(foundEvent.getStatus()));
        }

        if (eventUpdateRequestDto.getMaxPlaces() < foundEvent.getRegistrationList().size()) {
            throw new IllegalArgumentException("Максимальное количество мест превышает вместимость локации");
        }

        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Мероприятие не найдено по идентификатору=%s"
                        .formatted(eventId)));

        eventEntity.setName(eventUpdateRequestDto.getName());
        eventEntity.setMaxPlaces(eventUpdateRequestDto.getMaxPlaces());
        eventEntity.setDate(eventUpdateRequestDto.getDate());
        eventEntity.setCost(eventUpdateRequestDto.getCost());
        eventEntity.setDuration(eventUpdateRequestDto.getDuration());
        eventEntity.setLocationId(eventUpdateRequestDto.getLocationId());

        return converter.toDomain(eventRepository.save(eventEntity));
    }

    public List<Event> getMyEvents() {
        Integer currentUserId = authenticationService.getCurrentUser().getId();
        List<EventEntity> allEventsByOwnerId = eventRepository.findAllByOwnerId(currentUserId);
        if (allEventsByOwnerId.isEmpty()) {
            throw new EntityNotFoundException("Текущий пользователь еще не создавал мероприятия");
        }
        return allEventsByOwnerId
                .stream()
                .map(converter::toDomain)
                .toList();
    }

    public List<Event> searchEvents(EventSearchRequestDto eventSearchRequestDto) {
        List<EventEntity> eventEntities = eventRepository.searchEventsByFilter(
                eventSearchRequestDto.getName(),
                eventSearchRequestDto.getPlacesMin(),
                eventSearchRequestDto.getPlacesMax(),
                eventSearchRequestDto.getDateStartAfter(),
                eventSearchRequestDto.getDateStartBefore(),
                eventSearchRequestDto.getCostMin(),
                eventSearchRequestDto.getCostMax(),
                eventSearchRequestDto.getDurationMin(),
                eventSearchRequestDto.getDurationMax(),
                eventSearchRequestDto.getLocationId(),
                eventSearchRequestDto.getEventStatus()
        );
        if (eventEntities.isEmpty()) {
            throw new EntityNotFoundException("Мероприятия не найдены");
        }
        return eventEntities
                .stream()
                .map(converter::toDomain)
                .toList();
    }

    public void updateEventStatuses() {
        eventRepository.updateStartedEvents();
        eventRepository.updateEndedEvents();
    }

}
