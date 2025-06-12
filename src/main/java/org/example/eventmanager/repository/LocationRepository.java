package org.example.eventmanager.repository;

import org.example.eventmanager.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, Integer> {

    @Modifying
    @Query("""
                UPDATE LocationEntity l
                SET l.name = :name,
                    l.address = :address,
                    l.capacity = :capacity,
                    l.description = :description
                where l.id = :id
            """)
    void updateLocation(
            @Param("id") Integer id,
            @Param("name") String name,
            @Param("address") String address,
            @Param("capacity") Integer capacity,
            @Param("description") String description
    );
}
