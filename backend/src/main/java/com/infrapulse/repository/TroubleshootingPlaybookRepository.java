package com.infrapulse.repository;

import com.infrapulse.model.TroubleshootingPlaybook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TroubleshootingPlaybookRepository extends JpaRepository<TroubleshootingPlaybook, Long> {

    Optional<TroubleshootingPlaybook> findByConditionCode(String conditionCode);
}