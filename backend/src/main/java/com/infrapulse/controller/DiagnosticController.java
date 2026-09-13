package com.infrapulse.controller;

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
    public ResponseEntity<List<DiagnosticResult>> evaluateDiagnostics(@RequestParam Long serverId) {
        List<DiagnosticResult> results = diagnosticEngine.evaluate(serverId);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/health")
    public ResponseEntity<Map<String, Object>> checkHealth() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "Diagnostic Engine"));
    }
}