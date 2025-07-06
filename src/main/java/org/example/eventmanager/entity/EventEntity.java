package org.example.eventmanager.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "event")
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "owner_Id")
    private Integer ownerId;

    @Column(name = "max_place")
    private Integer maxPlaces;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "cost")
    private Integer cost;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "location_id")
    private Integer locationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @OneToMany(mappedBy = "event")
    private List<RegistrationEntity> registrationList;

    public EventEntity(
            Integer id,
            String name,
            Integer ownerId,
            Integer maxPlaces,
            LocalDateTime date,
            Integer cost,
            Integer duration,
            Integer locationId,
            EventStatus status,
            List<RegistrationEntity> registrationList
    ) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.maxPlaces = maxPlaces;
        this.date = date;
        this.cost = cost;
        this.duration = duration;
        this.locationId = locationId;
        this.status = status;
        this.registrationList = registrationList;
    }

    public EventEntity() {
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

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
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

    public List<RegistrationEntity> getRegistrationList() {
        return registrationList;
    }

    public void setRegistrationList(List<RegistrationEntity> registrationList) {
        this.registrationList = registrationList;
    }
}
