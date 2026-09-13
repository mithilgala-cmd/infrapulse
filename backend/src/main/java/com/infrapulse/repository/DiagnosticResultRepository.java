package com.infrapulse.repository;

import com.infrapulse.model.DiagnosticResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiagnosticResultRepository extends JpaRepository<DiagnosticResult, Long> {

    List<DiagnosticResult> findByServerIdOrderByTimestampDesc(Long serverId);

    List<DiagnosticResult> findByServerIdAndConditionCodeOrderByTimestampDesc(Long serverId, String conditionCode);

    List<DiagnosticResult> findByActiveTrueOrderByTimestampDesc();
}