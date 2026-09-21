package com.infrapulse.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "alert_state")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String conditionCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private Server server;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    @UpdateTimestamp
    private Instant lastSeen;

    private Instant cooldownUntil;
}