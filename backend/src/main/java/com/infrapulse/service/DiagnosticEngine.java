package com.infrapulse.service;

import com.infrapulse.model.*;
import com.infrapulse.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class DiagnosticEngine {

    private static final double CPU_THRESHOLD = 90.0;
    private static final double MEMORY_THRESHOLD = 90.0;
    private static final double SWAP_PRESSURE_THRESHOLD = 50.0;
    private static final double DISK_THRESHOLD = 90.0;
    private static final int NETWORK_ERROR_THRESHOLD = 0;

    private final MetricObservationRepository metricObservationRepository;
    private final DiagnosticResultRepository diagnosticResultRepository;
    private final ServerRepository serverRepository;
    private final TroubleshootingPlaybookRepository playbookRepository;

    public DiagnosticEngine(MetricObservationRepository metricObservationRepository,
                            DiagnosticResultRepository diagnosticResultRepository,
                            ServerRepository serverRepository,
                            TroubleshootingPlaybookRepository playbookRepository) {
        this.metricObservationRepository = metricObservationRepository;
        this.diagnosticResultRepository = diagnosticResultRepository;
        this.serverRepository = serverRepository;
        this.playbookRepository = playbookRepository;
    }

    @Transactional
    public List<DiagnosticResult> evaluate(Long serverId) {
        MetricObservation observation = metricObservationRepository.findRecentByServerId(serverId, 1).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No metrics found for server id: " + serverId));

        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new IllegalArgumentException("Server not found with id: " + serverId));

        List<DiagnosticResult> results = new ArrayList<>();

        Optional<DiagnosticResult> highCpu = checkHighCpu(observation);
        highCpu.ifPresent(results::add);

        Optional<DiagnosticResult> memoryPressure = checkMemoryPressure(observation);
        memoryPressure.ifPresent(results::add);

        Optional<DiagnosticResult> diskCapacity = checkDiskCapacity(observation);
        diskCapacity.ifPresent(results::add);

        Optional<DiagnosticResult> networkDegradation = checkNetworkDegradation(observation);
        networkDegradation.ifPresent(results::add);

        // Save all new diagnostic results
        for (DiagnosticResult result : results) {
            result.setServer(server);
            result.setMetricObservation(observation);
            result.setTimestamp(Instant.now());
            diagnosticResultRepository.save(result);
        }

        return results;
    }

    private Optional<DiagnosticResult> checkHighCpu(MetricObservation obs) {
        if (obs.getCpuPercentTotal() > CPU_THRESHOLD) {
            String evidence = String.format(
                    "CPU threshold exceeded: %.1f%% (threshold: %.1f%%). Process information available.",
                    obs.getCpuPercentTotal(), CPU_THRESHOLD);
            return Optional.of(DiagnosticResult.builder()
                    .conditionCode("HIGH_CPU")
                    .severity("HIGH")
                    .diagnosis("High CPU utilization")
                    .evidence(evidence)
                    .recommendedAction("Inspect top CPU-consuming processes")
                    .active(true)
                    .build());
        }
        return Optional.empty();
    }

    private Optional<DiagnosticResult> checkMemoryPressure(MetricObservation obs) {
        double memoryPercentUsed = obs.getMemoryTotalBytes() > 0
                ? ((obs.getMemoryTotalBytes() - obs.getMemoryAvailableBytes()) * 100.0 / obs.getMemoryTotalBytes())
                : 0.0;
        if (memoryPercentUsed > MEMORY_THRESHOLD && obs.getSwapPercentUsed() > SWAP_PRESSURE_THRESHOLD) {
            String evidence = String.format(
                    "Memory pressure detected: %.1f%% used, swap %.1f%% (thresholds: %.1f%% + %.1f%% swap)",
                    memoryPercentUsed, obs.getSwapPercentUsed(), MEMORY_THRESHOLD, SWAP_PRESSURE_THRESHOLD);
            return Optional.of(DiagnosticResult.builder()
                    .conditionCode("MEMORY_PRESSURE")
                    .severity("HIGH")
                    .diagnosis("Memory pressure")
                    .evidence(evidence)
                    .recommendedAction("Inspect memory-consuming processes")
                    .active(true)
                    .build());
        }
        return Optional.empty();
    }

    private Optional<DiagnosticResult> checkDiskCapacity(MetricObservation obs) {
        if (obs.getDiskPercentUsed() > DISK_THRESHOLD) {
            String evidence = String.format(
                    "Disk capacity threshold exceeded: %.1f%% used (threshold: %.1f%%)",
                    obs.getDiskPercentUsed(), DISK_THRESHOLD);
            return Optional.of(DiagnosticResult.builder()
                    .conditionCode("DISK_CAPACITY")
                    .severity("HIGH")
                    .diagnosis("Low disk capacity")
                    .evidence(evidence)
                    .recommendedAction("Identify large files and temporary data")
                    .active(true)
                    .build());
        }
        return Optional.empty();
    }

    private Optional<DiagnosticResult> checkNetworkDegradation(MetricObservation obs) {
        long totalErrors = obs.getNetworkErrin() + obs.getNetworkErrout()
                + obs.getNetworkDropin() + obs.getNetworkDropout();
        if (totalErrors > NETWORK_ERROR_THRESHOLD) {
            String evidence = String.format(
                    "Network errors detected: in=%d, out=%d, dropin=%d, dropout=%d",
                    obs.getNetworkErrin(), obs.getNetworkErrout(), obs.getNetworkDropin(), obs.getNetworkDropout());
            return Optional.of(DiagnosticResult.builder()
                    .conditionCode("NETWORK_DEGRADATION")
                    .severity("HIGH")
                    .diagnosis("Network connectivity degradation")
                    .evidence(evidence)
                    .recommendedAction("Check interface, gateway and connectivity")
                    .active(true)
                    .build());
        }
        return Optional.empty();
    }
}