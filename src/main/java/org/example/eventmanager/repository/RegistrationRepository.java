package org.example.eventmanager.repository;

import org.example.eventmanager.entity.RegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<RegistrationEntity, Integer> {

    @Transactional
    @Modifying
    @Query("""
            DELETE FROM RegistrationEntity r
            WHERE (r.event.id = :eventId AND r.userId = :userId)
            """)
    void deleteByUserIdAndEventId(@Param("userId") Integer userId, @Param("eventId") Integer eventId);

    List<RegistrationEntity> findByUserId(Integer id);

}
