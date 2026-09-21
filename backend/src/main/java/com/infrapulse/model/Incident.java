package com.infrapulse.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "incidents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private Server server;

    @Column(nullable = false, unique = true)
    private String incidentNumber;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String severity;

    @Column(nullable = false)
    private String symptom;

    @Column(nullable = false)
    private String detectedMetric;

    @Column(nullable = false)
    private String probableCause;

    @Column(nullable = false)
    private String evidence;

    @Column(nullable = false)
    private String recommendedAction;

    private String technicianNotes;

    @Column(columnDefinition = "TEXT[] NOT NULL DEFAULT '{}'")
    @Builder.Default
    private List<String> statusHistory = new ArrayList<>();

    private Instant resolutionTime;

    private String escalationReason;

    @Column(columnDefinition = "TEXT[] NOT NULL DEFAULT '{}'")
    @Builder.Default
    private List<String> troubleshootingHistory = new ArrayList<>();

    @Column(columnDefinition = "TEXT[] NOT NULL DEFAULT '{}'")
    @Builder.Default
    private List<String> evidenceLog = new ArrayList<>();

    private String recommendedNextStep;

    private Instant escalationTimestamp;

    @Column(nullable = false)
    @UpdateTimestamp
    private Instant updatedAt;

    @Column(nullable = false)
    private Instant createdAt;
}