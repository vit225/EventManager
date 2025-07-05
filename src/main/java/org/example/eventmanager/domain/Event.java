package org.example.eventmanager.domain;

import org.example.eventmanager.entity.EventStatus;

import java.time.LocalDateTime;
import java.util.List;

public class Event {

    private Integer id;


    private String name;

    private Integer ownerId;

    private LocalDateTime date;

    private List<Registration> registrationList;

    private Integer locationId;

    private EventStatus status;

    private Integer cost;


    private Integer duration;


    private Integer maxPlaces;

    public Event(
            Integer id,
            String name,
            Integer ownerId,
            LocalDateTime date,
            List<Registration> registrationList,
            Integer locationId,
            EventStatus status,
            Integer cost,
            Integer duration,
            Integer maxPlaces
    ) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.date = date;
        this.registrationList = registrationList;
        this.locationId = locationId;
        this.status = status;
        this.cost = cost;
        this.duration = duration;
        this.maxPlaces = maxPlaces;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getMaxPlaces() {
        return maxPlaces;
    }

    public void setMaxPlaces(Integer maxPlaces) {
        this.maxPlaces = maxPlaces;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public List<Registration> getRegistrationList() {
        return registrationList;
    }

    public void setRegistrationList(List<Registration> registrationList) {
        this.registrationList = registrationList;
    }
}
