package org.example.eventmanager;

import org.example.eventmanager.service.EventService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class EventStatusScheduler {

    private final EventService eventService;

    public EventStatusScheduler(EventService eventService) {
        this.eventService = eventService;
    }

    @Scheduled(fixedRate = 60000) //
    public void updateStatuses() {
        eventService.updateEventStatuses();
    }
}
