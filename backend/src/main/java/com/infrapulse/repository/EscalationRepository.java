package com.infrapulse.repository;

import com.infrapulse.model.Escalation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EscalationRepository extends JpaRepository<Escalation, Long> {

    List<Escalation> findByIncidentIdOrderByEscalationTimestampDesc(Long incidentId);
}