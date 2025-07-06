package org.example.eventmanager.repository;

import org.example.eventmanager.entity.EventEntity;
import org.example.eventmanager.entity.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Integer> {

    List<EventEntity> findAllByOwnerId(Integer id);

    @Query("""
            SELECT e FROM EventEntity e
            WHERE (:name IS NULL OR e.name LIKE %:name%)
            AND (:placesMin IS NULL OR e.maxPlaces >= :placesMin)
            AND (:placesMax IS NULL OR e.maxPlaces <= :placesMax)
            AND (CAST(:dateStartAfter as date) IS NULL OR e.date >= :dateStartAfter)
            AND (CAST(:dateStartBefore as date) IS NULL OR e.date <= :dateStartBefore)
            AND (:costMin IS NULL OR e.cost >= :costMin)
            AND (:costMax IS NULL OR e.cost <= :costMax)
            AND (:durationMin IS NULL OR e.duration >= :durationMin)
            AND (:durationMax IS NULL OR e.duration <= :durationMax)
            AND (:locationId IS NULL OR e.locationId = :locationId)
            AND (:eventStatus IS NULL OR e.status = :eventStatus)
            """)
    List<EventEntity> searchEventsByFilter(
            @Param("name") String name,
            @Param("placesMin") Integer placesMin,
            @Param("placesMax") Integer placesMax,
            @Param("dateStartAfter") LocalDateTime dateStartAfter,
            @Param("dateStartBefore") LocalDateTime dateStartBefore,
            @Param("costMin") Integer costMin,
            @Param("costMax") Integer costMax,
            @Param("durationMin") Integer durationMin,
            @Param("durationMax") Integer durationMax,
            @Param("locationId") Integer locationId,
            @Param("eventStatus") EventStatus eventStatus
    );


    @Transactional
    @Modifying
    @Query(value = """
            UPDATE event SET status = 'STARTED'
            WHERE date <= now()
            AND date + (duration * interval '1 minute') > now()
            AND status <> 'STARTED'
            AND status <> 'CANCELLED'
            """, nativeQuery = true)
    void updateStartedEvents();

    @Transactional
    @Modifying
    @Query(value = """
            UPDATE event SET status = 'FINISHED'
            WHERE date + (duration * interval '1 minute') <= now()
            AND status <> 'FINISHED'
            AND status <> 'CANCELLED'
            """, nativeQuery = true)
    void updateEndedEvents();
}
