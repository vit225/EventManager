package org.example.eventmanager.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.eventmanager.converter.EventEntityConverter;
import org.example.eventmanager.domain.Event;
import org.example.eventmanager.domain.User;
import org.example.eventmanager.entity.EventEntity;
import org.example.eventmanager.entity.EventStatus;
import org.example.eventmanager.entity.RegistrationEntity;
import org.example.eventmanager.repository.EventRepository;
import org.example.eventmanager.repository.RegistrationRepository;
import org.example.eventmanager.security.jwt.AuthenticationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final AuthenticationService authenticationService;
    private final EventRepository eventRepository;
    private final EventEntityConverter eventEntityConverter;

    public RegistrationService(RegistrationRepository registrationRepository, AuthenticationService authenticationService, EventRepository eventRepository, EventEntityConverter eventEntityConverter) {
        this.registrationRepository = registrationRepository;
        this.authenticationService = authenticationService;
        this.eventRepository = eventRepository;
        this.eventEntityConverter = eventEntityConverter;
    }

    public void registerOnEvent(Integer eventId) {
        User currentUser = authenticationService.getCurrentUser();
        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Мероприятие не найдено по идентификатору=%s"
                        .formatted(eventId)));
        if (eventEntity.getStatus().equals(EventStatus.FINISHED) || eventEntity.getStatus().equals(EventStatus.CANCELLED)) {
            throw new IllegalArgumentException("Нельзя зарегистрироваться на мероприятие со статусом=%s"
                    .formatted(eventEntity.getStatus().name()));
        }
        registrationRepository.save(new RegistrationEntity(null, eventEntity, currentUser.getId()));
    }

    public void cancelRegister(Integer eventId) {
        User currentUser = authenticationService.getCurrentUser();
        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Мероприятие не найдено по идентификатору=%s"
                        .formatted(eventId)));
        if (eventEntity.getStatus().equals(EventStatus.STARTED) || eventEntity.getStatus().equals(EventStatus.FINISHED)) {
            throw new IllegalArgumentException("Нельзя отменить регистрацию на мероприятие со статусом=%s"
                    .formatted(eventEntity.getStatus().name()));
        }
        registrationRepository.deleteByUserIdAndEventId(currentUser.getId(), eventEntity.getId());
    }

    public List<Event> findEventsByUserId() {
        User currentUser = authenticationService.getCurrentUser();
        List<RegistrationEntity> registrationEntities = registrationRepository.findByUserId(currentUser.getId());

        return registrationEntities.stream()
                .map((reg -> eventEntityConverter.toDomain(reg.getEvent())))
                .toList();
    }
}
