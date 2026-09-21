package com.infrapulse.service;

import com.infrapulse.dto.*;
import com.infrapulse.model.*;
import com.infrapulse.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@Transactional
public class IncidentService {

    public static final Set<String> VALID_TRANSITIONS = Set.of(
            "NEW:ACKNOWLEDGED",
            "ACKNOWLEDGED:INVESTIGATING",
            "INVESTIGATING:MITIGATION_APPLIED",
            "MITIGATION_APPLIED:RESOLVED",
            "RESOLVED:CLOSED"
    );

    private final IncidentRepository incidentRepository;
    private final IncidentStatusHistoryRepository statusHistoryRepository;
    private final EscalationRepository escalationRepository;
    private final ServerRepository serverRepository;

    public IncidentService(IncidentRepository incidentRepository,
                           IncidentStatusHistoryRepository statusHistoryRepository,
                           EscalationRepository escalationRepository,
                           ServerRepository serverRepository) {
        this.incidentRepository = incidentRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.escalationRepository = escalationRepository;
        this.serverRepository = serverRepository;
    }

    @Transactional
    public IncidentDto createIncident(IncidentCreateRequest request) {
        Server server = serverRepository.findById(request.getServerId())
                .orElseThrow(() -> new EntityNotFoundException("Server not found with id: " + request.getServerId()));
        Incident incident = Incident.builder()
                .server(server)
                .incidentNumber(generateIncidentNumber())
                .status("NEW")
                .severity(request.getSeverity())
                .symptom(request.getSymptom())
                .detectedMetric(request.getDetectedMetric())
                .probableCause(request.getProbableCause())
                .evidence(request.getEvidence())
                .recommendedAction(request.getRecommendedAction())
                .technicianNotes(request.getTechnicianNotes())
                .statusHistory(new ArrayList<>(List.of("NEW")))
                .troubleshootingHistory(new ArrayList<>())
                .evidenceLog(new ArrayList<>())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Incident saved = incidentRepository.save(incident);
        addStatusHistory(saved, null, "NEW", null);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public IncidentDto getIncident(Long id) {
        Incident incident = findByIdOrThrow(id);
        return toDto(incident);
    }

    @Transactional(readOnly = true)
    public List<IncidentDto> getAllIncidents() {
        return incidentRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<IncidentDto> getIncidentsByServerId(Long serverId) {
        return incidentRepository.findByServerIdOrderByCreatedAtDesc(serverId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public IncidentDto updateStatus(Long id, IncidentStatusTransition transition) {
        Incident incident = findByIdOrThrow(id);
        validateTransition(incident.getStatus(), transition.getStatus());

        String fromStatus = incident.getStatus();
        incident.setStatus(transition.getStatus());
        incident.setUpdatedAt(Instant.now());

        addStatusHistory(incident, fromStatus, transition.getStatus(), transition.getNotes());

        Incident saved = incidentRepository.save(incident);
        return toDto(saved);
    }

    @Transactional
    public IncidentDto escalateIncident(Long id, EscalationDto escalationDto) {
        Incident incident = findByIdOrThrow(id);

        if (!List.of("INVESTIGATING", "MITIGATION_APPLIED").contains(incident.getStatus())) {
            throw new IllegalStateException(
                    "Incident can only be escalated from INVESTIGATING or MITIGATION_APPLIED status. Current: " + incident.getStatus());
        }

        Escalation escalation = Escalation.builder()
                .incident(incident)
                .reason(escalationDto.getReason())
                .troubleshootingPerformed(escalationDto.getTroubleshootingPerformed() != null
                        ? Arrays.asList(escalationDto.getTroubleshootingPerformed())
                        : new ArrayList<>())
                .evidence(escalationDto.getEvidence() != null
                        ? Arrays.asList(escalationDto.getEvidence())
                        : new ArrayList<>())
                .recommendedNextStep(escalationDto.getRecommendedNextStep())
                .escalationTimestamp(Instant.now())
                .escalatedTo("L2")
                .build();

        incident.setEscalationReason(escalationDto.getReason());
        incident.setEscalationTimestamp(Instant.now());
        incident.setRecommendedNextStep(escalationDto.getRecommendedNextStep());
        incident.getTroubleshootingHistory().add("Escalated to L2: " + escalationDto.getReason());
        incident.setUpdatedAt(Instant.now());

        escalationRepository.save(escalation);
        Incident saved = incidentRepository.save(incident);
        return toDto(saved);
    }

    private void validateTransition(String currentStatus, String newStatus) {
        String transitionKey = currentStatus + ":" + newStatus;
        if (!VALID_TRANSITIONS.contains(transitionKey)) {
            throw new IllegalStateException(
                    String.format("Invalid status transition: %s → %s. Valid transitions follow the lifecycle: NEW → ACKNOWLEDGED → INVESTIGATING → MITIGATION_APPLIED → RESOLVED → CLOSED",
                            currentStatus, newStatus));
        }
    }

    private void addStatusHistory(Incident incident, String fromStatus, String toStatus, String notes) {
        IncidentStatusHistory history = IncidentStatusHistory.builder()
                .incident(incident)
                .fromStatus(fromStatus)
                .toStatus(toStatus)
                .timestamp(Instant.now())
                .technicianNotes(notes)
                .build();
        statusHistoryRepository.save(history);
        incident.getStatusHistory().add(toStatus);
    }

    private Incident findByIdOrThrow(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Incident not found with id: " + id));
    }

    private String generateIncidentNumber() {
        return "INC-" + System.currentTimeMillis();
    }

    private IncidentDto toDto(Incident incident) {
        return IncidentDto.builder()
                .id(incident.getId())
                .incidentNumber(incident.getIncidentNumber())
                .status(incident.getStatus())
                .severity(incident.getSeverity())
                .symptom(incident.getSymptom())
                .detectedMetric(incident.getDetectedMetric())
                .probableCause(incident.getProbableCause())
                .evidence(incident.getEvidence())
                .recommendedAction(incident.getRecommendedAction())
                .technicianNotes(incident.getTechnicianNotes())
                .statusHistory(incident.getStatusHistory())
                .resolutionTime(incident.getResolutionTime())
                .escalationReason(incident.getEscalationReason())
                .troubleshootingHistory(incident.getTroubleshootingHistory())
                .evidenceLog(incident.getEvidenceLog())
                .recommendedNextStep(incident.getRecommendedNextStep())
                .escalationTimestamp(incident.getEscalationTimestamp())
                .createdAt(incident.getCreatedAt())
                .updatedAt(incident.getUpdatedAt())
                .build();
    }
}