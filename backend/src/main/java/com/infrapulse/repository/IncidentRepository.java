package com.infrapulse.repository;

import com.infrapulse.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    List<Incident> findByServerIdOrderByCreatedAtDesc(Long serverId);

    List<Incident> findByStatusOrderByCreatedAtDesc(String status);

    List<Incident> findAllByOrderByCreatedAtDesc();
}