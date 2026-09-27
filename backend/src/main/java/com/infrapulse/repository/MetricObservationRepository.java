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

    @Query(value = "SELECT * FROM metric_observations WHERE server_id = :serverId ORDER BY timestamp DESC LIMIT :limit",
            nativeQuery = true)
    List<MetricObservation> findRecentByServerId(@Param("serverId") Long serverId, @Param("limit") int limit);
}