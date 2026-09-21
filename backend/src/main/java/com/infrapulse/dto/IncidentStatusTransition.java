package com.infrapulse.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IncidentStatusTransition {

    @NotBlank
    private String status;

    private String notes;
}