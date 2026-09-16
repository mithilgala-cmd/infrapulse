package com.infrapulse.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "troubleshooting_playbooks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TroubleshootingPlaybook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String conditionCode;

    @Column(nullable = false)
    private String title;

    @Column
    @Builder.Default
    private List<String> symptoms = new java.util.ArrayList<>();

    @Column
    @Builder.Default
    private List<String> checks = new java.util.ArrayList<>();

    @Column
    @Builder.Default
    private List<String> possibleCauses = new java.util.ArrayList<>();

    @Column
    @Builder.Default
    private List<String> recommendedActions = new java.util.ArrayList<>();

    @Column
    @Builder.Default
    private List<String> escalationConditions = new java.util.ArrayList<>();
}