package com.infrapulse.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosticResultDto {

    private Long id;
    private Long serverId;
    private Long metricObservationId;
    private Instant timestamp;
    private String conditionCode;
    private String severity;
    private String diagnosis;
    private String evidence;
    private String recommendedAction;
    private boolean active;
}
