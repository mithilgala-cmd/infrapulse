package com.infrapulse.controller;

import com.infrapulse.dto.*;
import com.infrapulse.service.IncidentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PostMapping
    public ResponseEntity<IncidentDto> createIncident(@Valid @RequestBody IncidentCreateRequest request) {
        return ResponseEntity.ok(incidentService.createIncident(request));
    }

    @GetMapping
    public ResponseEntity<List<IncidentDto>> getAllIncidents() {
        return ResponseEntity.ok(incidentService.getAllIncidents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentDto> getIncident(@PathVariable Long id) {
        return ResponseEntity.ok(incidentService.getIncident(id));
    }

    @GetMapping("/server/{serverId}")
    public ResponseEntity<List<IncidentDto>> getIncidentsByServerId(@PathVariable Long serverId) {
        return ResponseEntity.ok(incidentService.getIncidentsByServerId(serverId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<IncidentDto> updateStatus(@PathVariable Long id,
                                                     @Valid @RequestBody IncidentStatusTransition transition) {
        return ResponseEntity.ok(incidentService.updateStatus(id, transition));
    }

    @PostMapping("/{id}/escalate")
    public ResponseEntity<IncidentDto> escalateIncident(@PathVariable Long id,
                                                          @Valid @RequestBody EscalationDto escalationDto) {
        return ResponseEntity.ok(incidentService.escalateIncident(id, escalationDto));
    }
}