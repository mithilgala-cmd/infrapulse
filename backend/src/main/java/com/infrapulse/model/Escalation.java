package com.infrapulse.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "escalations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Escalation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    @Column(nullable = false)
    private String reason;

    @Column
    @Builder.Default
    private List<String> troubleshootingPerformed = new ArrayList<>();

    @Column
    @Builder.Default
    private List<String> evidence = new ArrayList<>();

    @Column(nullable = false)
    private String recommendedNextStep;

    @Column(nullable = false)
    private Instant escalationTimestamp;

    @Column(nullable = false)
    private String escalatedTo;
}