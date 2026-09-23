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
    @Column(name = "cpu_percent_total", nullable = false)
    private double cpuPercentTotal;
    @Column(name = "cpu_percent_user", nullable = false)
    private double cpuPercentUser;
    @Column(name = "cpu_percent_system", nullable = false)
    private double cpuPercentSystem;
    @Column(name = "cpu_percent_idle", nullable = false)
    private double cpuPercentIdle;
    @Column(name = "cpu_percent_iowait", nullable = false)
    private double cpuPercentIowait;
    @Column(name = "load_average_1m", nullable = false)
    private double loadAverage1m;
    @Column(name = "load_average_5m", nullable = false)
    private double loadAverage5m;
    @Column(name = "load_average_15m", nullable = false)
    private double loadAverage15m;
    @Column(name = "num_cpu_cores", nullable = false)
    private int numCpuCores;
    @Column(name = "num_logical_cpus", nullable = false)
    private int numLogicalCpus;

    // Memory
    @Column(name = "memory_total_bytes", nullable = false)
    private long memoryTotalBytes;
    @Column(name = "memory_available_bytes", nullable = false)
    private long memoryAvailableBytes;
    @Column(name = "memory_used_bytes", nullable = false)
    private long memoryUsedBytes;
    @Column(name = "memory_free_bytes", nullable = false)
    private long memoryFreeBytes;
    @Column(name = "memory_active_bytes", nullable = false)
    private long memoryActiveBytes;
    @Column(name = "memory_inactive_bytes", nullable = false)
    private long memoryInactiveBytes;
    @Column(name = "memory_buffers_bytes", nullable = false)
    private long memoryBuffersBytes;
    @Column(name = "memory_cached_bytes", nullable = false)
    private long memoryCachedBytes;
    @Column(name = "swap_total_bytes", nullable = false)
    private long swapTotalBytes;
    @Column(name = "swap_used_bytes", nullable = false)
    private long swapUsedBytes;
    @Column(name = "swap_free_bytes", nullable = false)
    private long swapFreeBytes;
    @Column(name = "swap_percent_used", nullable = false)
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
    @Column(name = "disk_io_read_bytes", nullable = false)
    private long diskIOReadBytes;
    @Column(name = "disk_io_write_bytes", nullable = false)
    private long diskIOWriteBytes;
    @Column(name = "disk_io_read_count", nullable = false)
    private long diskIOReadCount;
    @Column(name = "disk_io_write_count", nullable = false)
    private long diskIOWriteCount;

    // Network
    @Column(name = "network_bytes_sent", nullable = false)
    private long networkBytesSent;
    @Column(name = "network_bytes_recv", nullable = false)
    private long networkBytesRecv;
    @Column(name = "network_packets_sent", nullable = false)
    private long networkPacketsSent;
    @Column(name = "network_packets_recv", nullable = false)
    private long networkPacketsRecv;
    @Column(name = "network_errin", nullable = false)
    private long networkErrin;
    @Column(name = "network_errout", nullable = false)
    private long networkErrout;
    @Column(name = "network_dropin", nullable = false)
    private long networkDropin;
    @Column(name = "network_dropout", nullable = false)
    private long networkDropout;

    // System
    @Column(name = "process_count", nullable = false)
    private int processCount;
    @Column(name = "uptime_seconds", nullable = false)
    private double uptimeSeconds;
    @Column(name = "boot_time", nullable = false)
    private Instant bootTime;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}