package com.infrapulse.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentDto {

    private Long id;
    private String incidentNumber;
    private String status;
    private String severity;
    private String symptom;
    private String detectedMetric;
    private String probableCause;
    private String evidence;
    private String recommendedAction;
    private String technicianNotes;
    private List<String> statusHistory;
    private Instant resolutionTime;
    private String escalationReason;
    private List<String> troubleshootingHistory;
    private List<String> evidenceLog;
    private String recommendedNextStep;
    private Instant escalationTimestamp;
    private Instant createdAt;
    private Instant updatedAt;
    private ServerInfo server;
    private DiagnosticInfo diagnostic;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ServerInfo {
        private Long id;
        private String hostname;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DiagnosticInfo {
        private String conditionCode;
        private String diagnosis;
        private String evidence;
        private String recommendedAction;
    }
}