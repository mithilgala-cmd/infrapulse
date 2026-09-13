package com.infrapulse.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "metric_observations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetricObservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private Server server;

    @Column(nullable = false)
    private Instant timestamp;

    // CPU
    @Column(nullable = false)
    private double cpuPercentTotal;
    @Column(nullable = false)
    private double cpuPercentUser;
    @Column(nullable = false)
    private double cpuPercentSystem;
    @Column(nullable = false)
    private double cpuPercentIdle;
    @Column(nullable = false)
    private double cpuPercentIowait;
    @Column(nullable = false)
    private double loadAverage1m;
    @Column(nullable = false)
    private double loadAverage5m;
    @Column(nullable = false)
    private double loadAverage15m;
    @Column(nullable = false)
    private int numCpuCores;
    @Column(nullable = false)
    private int numLogicalCpus;

    // Memory
    @Column(nullable = false)
    private long memoryTotalBytes;
    @Column(nullable = false)
    private long memoryAvailableBytes;
    @Column(nullable = false)
    private long memoryUsedBytes;
    @Column(nullable = false)
    private long memoryFreeBytes;
    @Column(nullable = false)
    private long memoryActiveBytes;
    @Column(nullable = false)
    private long memoryInactiveBytes;
    @Column(nullable = false)
    private long memoryBuffersBytes;
    @Column(nullable = false)
    private long memoryCachedBytes;
    @Column(nullable = false)
    private long swapTotalBytes;
    @Column(nullable = false)
    private long swapUsedBytes;
    @Column(nullable = false)
    private long swapFreeBytes;
    @Column(nullable = false)
    private double swapPercentUsed;

    // Disk
    @Column(nullable = false)
    private long diskTotalBytes;
    @Column(nullable = false)
    private long diskUsedBytes;
    @Column(nullable = false)
    private long diskFreeBytes;
    @Column(nullable = false)
    private double diskPercentUsed;
    @Column(nullable = false)
    private long diskIOReadBytes;
    @Column(nullable = false)
    private long diskIOWriteBytes;
    @Column(nullable = false)
    private long diskIOReadCount;
    @Column(nullable = false)
    private long diskIOWriteCount;

    // Network
    @Column(nullable = false)
    private long networkBytesSent;
    @Column(nullable = false)
    private long networkBytesRecv;
    @Column(nullable = false)
    private long networkPacketsSent;
    @Column(nullable = false)
    private long networkPacketsRecv;
    @Column(nullable = false)
    private long networkErrin;
    @Column(nullable = false)
    private long networkErrout;
    @Column(nullable = false)
    private long networkDropin;
    @Column(nullable = false)
    private long networkDropout;

    // System
    @Column(nullable = false)
    private int processCount;
    @Column(nullable = false)
    private double uptimeSeconds;
    @Column(nullable = false)
    private Instant bootTime;

    @Column(nullable = false)
    private Instant createdAt;
}