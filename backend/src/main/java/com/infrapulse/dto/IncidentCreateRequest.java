package com.infrapulse.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentCreateRequest {

    @NotNull
    private Long serverId;

    @NotBlank
    private String severity;

    @NotBlank
    private String symptom;

    @NotBlank
    private String detectedMetric;

    @NotBlank
    private String probableCause;

    @NotBlank
    private String evidence;

    @NotBlank
    private String recommendedAction;

    private String technicianNotes;
}