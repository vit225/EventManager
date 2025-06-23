package org.example.eventmanager;

import org.example.eventmanager.service.UserService;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private final UserService userService;

    public DataInitializer(UserService userService) {
        this.userService = userService;
    }

    @EventListener(ApplicationStartedEvent.class)
    public void onApplicationEvent() {
        userService.createDefaultUsers();
    }
}
