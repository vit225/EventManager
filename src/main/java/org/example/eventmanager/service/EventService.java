package org.example.eventmanager.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.eventmanager.converter.EventEntityConverter;
import org.example.eventmanager.domain.Event;
import org.example.eventmanager.domain.Location;
import org.example.eventmanager.domain.Role;
import org.example.eventmanager.domain.User;
import org.example.eventmanager.dto.EventCreateRequestDto;
import org.example.eventmanager.dto.EventSearchRequestDto;
import org.example.eventmanager.dto.EventUpdateRequestDto;
import org.example.eventmanager.entity.EventEntity;
import org.example.eventmanager.entity.EventStatus;
import org.example.eventmanager.entity.RegistrationEntity;
import org.example.eventmanager.kafka.KafkaEvent;
import org.example.eventmanager.kafka.EventSender;
import org.example.eventmanager.repository.EventRepository;
import org.example.eventmanager.security.jwt.AuthenticationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventEntityConverter converter;
    private final AuthenticationService authenticationService;
    private final LocationService locationService;
    private final EventSender eventSender;

    public EventService(
            EventRepository eventRepository,
            EventEntityConverter converter,
            AuthenticationService authenticationService,
            LocationService locationService, EventSender eventSender
    ) {
        this.eventRepository = eventRepository;
        this.converter = converter;
        this.authenticationService = authenticationService;
        this.locationService = locationService;
        this.eventSender = eventSender;
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
        EventEntity oldEventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Мероприятие не найдено по идентификатору=%s"
                        .formatted(eventId)));


        if (!currentUser.getId().equals(oldEventEntity.getOwnerId()) && currentUser.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("У текущего пользователя нет прав для обновления мероприятия");
        }

        if (!oldEventEntity.getStatus().equals(EventStatus.WAIT_START)) {
            throw new IllegalArgumentException("Невозможно изменить мероприятие статус которого: %s"
                    .formatted(oldEventEntity.getStatus()));
        }

        if (eventUpdateRequestDto.getMaxPlaces() < oldEventEntity.getRegistrationList().size()) {
            throw new IllegalArgumentException("Максимальное количество мест превышает вместимость локации");
        }

        EventEntity oldEventCopy = new EventEntity();
        BeanUtils.copyProperties(oldEventEntity, oldEventCopy);

        oldEventEntity.setName(eventUpdateRequestDto.getName());
        oldEventEntity.setMaxPlaces(eventUpdateRequestDto.getMaxPlaces());
        oldEventEntity.setDate(eventUpdateRequestDto.getDate());
        oldEventEntity.setCost(eventUpdateRequestDto.getCost());
        oldEventEntity.setDuration(eventUpdateRequestDto.getDuration());
        oldEventEntity.setLocationId(eventUpdateRequestDto.getLocationId());

        EventEntity updatedEventEntity = eventRepository.save(oldEventEntity);
        Map<String, KafkaEvent.ChangedField> changedFields = detectChanges(oldEventCopy, updatedEventEntity);
        if (!changedFields.isEmpty()) {
            KafkaEvent kafkaEvent = new KafkaEvent();

            List<Integer> subscriberIds = updatedEventEntity.getRegistrationList()
                    .stream()
                    .map(RegistrationEntity::getUserId)
                    .distinct()
                    .collect(Collectors.toList());

            kafkaEvent.setEventId(updatedEventEntity.getId());
            kafkaEvent.setUserIdWhoChanged(currentUser.getId());
            kafkaEvent.setOwnerId(updatedEventEntity.getOwnerId());
            kafkaEvent.setChangedFields(changedFields);
            kafkaEvent.setSubscriberUserIds(subscriberIds);

            eventSender.sendEvent(kafkaEvent);
        }
        return converter.toDomain(eventRepository.save(oldEventEntity));
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


    @Transactional
    public void updateEventStatuses() {
        List<EventEntity> toStart = eventRepository.findEventsToStart();
        for (EventEntity event : toStart) {
            EventStatus oldStatus = event.getStatus();
            event.setStatus(EventStatus.STARTED);
            eventRepository.save(event);

            sendStatusChangeEvent(event, oldStatus, EventStatus.STARTED);
        }

        List<EventEntity> toFinish = eventRepository.findEventsToFinish();
        for (EventEntity event : toFinish) {
            EventStatus oldStatus = event.getStatus();
            event.setStatus(EventStatus.FINISHED);
            eventRepository.save(event);

            sendStatusChangeEvent(event, oldStatus, EventStatus.FINISHED);
        }
    }

    private void sendStatusChangeEvent(EventEntity event, EventStatus oldStatus, EventStatus newStatus) {
        KafkaEvent dto = new KafkaEvent();

        Map<String, KafkaEvent.ChangedField> changedField  = new HashMap<>();
        changedField.put("status", new KafkaEvent.ChangedField(oldStatus, newStatus));

        List<Integer> subscriberIds = event.getRegistrationList()
                .stream()
                .map(RegistrationEntity::getUserId)
                .distinct()
                .toList();

        dto.setChangedFields(changedField);
        dto.setEventId(event.getId());
        dto.setSubscriberUserIds(subscriberIds);
        dto.setOwnerId(event.getOwnerId());

        eventSender.sendEvent(dto);
    }


    private Map<String, KafkaEvent.ChangedField> detectChanges(EventEntity oldEventEntity, EventEntity newEventEntity) {
        Map<String, KafkaEvent.ChangedField> changedFields = new HashMap<>();

        if (!Objects.equals(oldEventEntity.getName(), newEventEntity.getName())) {
            changedFields.put("name", new KafkaEvent.ChangedField(oldEventEntity.getName(), newEventEntity.getName()));
        }

        if (!Objects.equals(oldEventEntity.getCost(), newEventEntity.getCost())) {
            changedFields.put("cost", new KafkaEvent.ChangedField(oldEventEntity.getCost(), newEventEntity.getCost()));
        }

        if (!Objects.equals(oldEventEntity.getDate(), newEventEntity.getDate())) {
            changedFields.put("date", new KafkaEvent.ChangedField(oldEventEntity.getDate(), newEventEntity.getDate()));
        }

        if (!Objects.equals(oldEventEntity.getDuration(), newEventEntity.getDuration())) {
            changedFields.put("duration", new KafkaEvent.ChangedField(oldEventEntity.getDuration(), newEventEntity.getDuration()));
        }

        if (!Objects.equals(oldEventEntity.getMaxPlaces(), newEventEntity.getMaxPlaces())) {
            changedFields.put("maxPlaces", new KafkaEvent.ChangedField(oldEventEntity.getMaxPlaces(), newEventEntity.getMaxPlaces()));
        }

        if (!Objects.equals(oldEventEntity.getLocationId(), newEventEntity.getLocationId())) {
            changedFields.put("locationId", new KafkaEvent.ChangedField(oldEventEntity.getLocationId(), newEventEntity.getLocationId()));
        }

        return changedFields;
    }
}
