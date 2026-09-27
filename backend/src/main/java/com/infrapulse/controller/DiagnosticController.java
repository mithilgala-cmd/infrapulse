package com.infrapulse.controller;

import com.infrapulse.dto.DiagnosticResultDto;
import com.infrapulse.model.DiagnosticResult;
import com.infrapulse.service.DiagnosticEngine;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/diagnostics")
public class DiagnosticController {

    private final DiagnosticEngine diagnosticEngine;

    public DiagnosticController(DiagnosticEngine diagnosticEngine) {
        this.diagnosticEngine = diagnosticEngine;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<List<DiagnosticResultDto>> evaluateDiagnostics(@RequestParam Long serverId) {
        List<DiagnosticResult> results = diagnosticEngine.evaluate(serverId);
        return ResponseEntity.ok(results.stream().map(this::toDto).toList());
    }

    @PostMapping("/health")
    public ResponseEntity<Map<String, Object>> checkHealth() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "Diagnostic Engine"));
    }

    private DiagnosticResultDto toDto(DiagnosticResult result) {
        return DiagnosticResultDto.builder()
                .id(result.getId())
                .serverId(result.getServer() != null ? result.getServer().getId() : null)
                .metricObservationId(result.getMetricObservation() != null
                        ? result.getMetricObservation().getId() : null)
                .timestamp(result.getTimestamp())
                .conditionCode(result.getConditionCode())
                .severity(result.getSeverity())
                .diagnosis(result.getDiagnosis())
                .evidence(result.getEvidence())
                .recommendedAction(result.getRecommendedAction())
                .active(result.isActive())
                .build();
    }
}