package org.example.eventmanager.controller;

import jakarta.validation.Valid;
import org.example.eventmanager.converter.EventDtoConverter;
import org.example.eventmanager.domain.Event;
import org.example.eventmanager.dto.EventCreateRequestDto;
import org.example.eventmanager.dto.EventDto;
import org.example.eventmanager.dto.EventSearchRequestDto;
import org.example.eventmanager.dto.EventUpdateRequestDto;
import org.example.eventmanager.service.EventService;
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
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;
    private final EventDtoConverter converterDto;
    private final Logger log = LoggerFactory.getLogger(EventController.class);

    public EventController(EventService eventService, EventDtoConverter converterDto) {
        this.eventService = eventService;
        this.converterDto = converterDto;
    }

    @PostMapping()
    public ResponseEntity<EventDto> createEvent(@RequestBody @Valid EventCreateRequestDto eventRequestDto) {
        log.info("Get request for create event: event={}", eventRequestDto);
        Event createdEvent = eventService.createEvent(eventRequestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(converterDto.toDto(createdEvent));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Integer eventId) {
        log.info("Get request for delete event: id={}", eventId);
        eventService.deleteEvent(eventId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getEvent(@PathVariable Integer eventId) {
        log.info("Get request for get event: id={}", eventId);
        Event foundEvent = eventService.findEventById(eventId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(converterDto.toDto(foundEvent));
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventDto> updateEvent(@PathVariable Integer eventId, @RequestBody EventUpdateRequestDto eventUpdateRequestDto) {
        log.info("Get request for update event: id={}", eventId);
        Event updateEvent = eventService.updateEvent(eventId, eventUpdateRequestDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(converterDto.toDto(updateEvent));
    }

    @GetMapping("/my")
    public ResponseEntity<List<EventDto>> getMyEvents() {
        log.info("Get a request to receive the current user's events");
        List<Event> myEvents = eventService.getMyEvents();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(myEvents.stream()
                        .map(converterDto::toDto)
                        .toList());
    }

    @PostMapping("/search")
    public ResponseEntity<List<EventDto>> searchEvents(@RequestBody EventSearchRequestDto eventSearchRequestDto) {
        log.info("Get a request to receive filter events");
        List<Event> events = eventService.searchEvents(eventSearchRequestDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(events.stream()
                        .map(converterDto::toDto)
                        .toList());
    }
}
