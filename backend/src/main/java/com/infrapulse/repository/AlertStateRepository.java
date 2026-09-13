package com.infrapulse.repository;

import com.infrapulse.model.AlertState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlertStateRepository extends JpaRepository<AlertState, Long> {

    Optional<AlertState> findByServerIdAndConditionCode(Long serverId, String conditionCode);
}