package org.example.eventmanager.controller;

import org.example.eventmanager.converter.EventDtoConverter;
import org.example.eventmanager.dto.EventDto;
import org.example.eventmanager.service.RegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/events/registrations")
public class RegistrationController {

    public final RegistrationService registrationService;
    private final EventDtoConverter eventDtoConverter;
    private final Logger log = LoggerFactory.getLogger(RegistrationController.class);

    public RegistrationController(RegistrationService registrationService, EventDtoConverter eventDtoConverter) {
        this.registrationService = registrationService;
        this.eventDtoConverter = eventDtoConverter;
    }

    @PostMapping("/{eventId}")
    public ResponseEntity<Void> registerEvent(@PathVariable("eventId") Integer eventId) {
        log.info("Запрос на регистрацию пользователя на мероприятие с ID={}", eventId);
        registrationService.registerOnEvent(eventId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/cancel/{eventId}")
    public ResponseEntity<Void> cancelRegister(@PathVariable Integer eventId) {
        log.info("Запрос на отмену регистрации пользователя на мероприятие с ID={}", eventId);
        registrationService.cancelRegister(eventId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/my")
    public List<EventDto> getMyRegisteredEvents() {
        log.info("Запрос списка мероприятий, на которые пользователь зарегистрирован");
        return registrationService.findEventsByUserId().stream()
                .map(eventDtoConverter::toDto)
                .toList();
    }
}
