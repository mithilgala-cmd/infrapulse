package com.infrapulse.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "diagnostic_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosticResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private Server server;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metric_observation_id")
    private MetricObservation metricObservation;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(nullable = false)
    private String conditionCode;

    @Column(nullable = false)
    private String severity;

    @Column(nullable = false)
    private String diagnosis;

    @Column(nullable = false)
    private String evidence;

    @Column(nullable = false)
    private String recommendedAction;

    @Column(nullable = false)
    private boolean active;
}