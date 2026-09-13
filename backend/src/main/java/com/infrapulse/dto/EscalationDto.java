package com.infrapulse.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EscalationDto {

    @NotBlank
    private String reason;

    private String[] troubleshootingPerformed;

    private String[] evidence;

    @NotBlank
    private String recommendedNextStep;
}