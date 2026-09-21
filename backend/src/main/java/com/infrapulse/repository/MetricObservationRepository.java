package com.infrapulse.repository;

import com.infrapulse.model.MetricObservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MetricObservationRepository extends JpaRepository<MetricObservation, Long> {

    List<MetricObservation> findByServerIdOrderByTimestampDesc(Long serverId);

    List<MetricObservation> findByServerIdAndTimestampBetweenOrderByTimestampDesc(
            Long serverId, Instant start, Instant end);

    @Query("SELECT m FROM MetricObservation m WHERE m.server.id = :serverId ORDER BY m.timestamp DESC LIMIT :limit")
    List<MetricObservation> findRecentByServerId(@Param("serverId") Long serverId, @Param("limit") int limit);
}