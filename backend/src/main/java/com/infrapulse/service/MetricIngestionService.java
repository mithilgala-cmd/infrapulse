package com.infrapulse.service;

import com.infrapulse.dto.MetricIngestionRequest;
import com.infrapulse.model.MetricObservation;
import com.infrapulse.model.Server;
import com.infrapulse.repository.MetricObservationRepository;
import com.infrapulse.repository.ServerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class MetricIngestionService {

    private final ServerRepository serverRepository;
    private final MetricObservationRepository metricObservationRepository;

    public MetricIngestionService(ServerRepository serverRepository,
                                  MetricObservationRepository metricObservationRepository) {
        this.serverRepository = serverRepository;
        this.metricObservationRepository = metricObservationRepository;
    }

    @Transactional
    public MetricObservation ingest(MetricIngestionRequest request) {
        Server server = serverRepository.findByHostname(request.getHostname())
                .orElseGet(() -> {
                    Server newServer = Server.builder()
                            .hostname(request.getHostname())
                            .agentId("agent-" + request.getHostname())
                            .agentVersion("0.1.0")
                            .createdAt(Instant.now())
                            .updatedAt(Instant.now())
                            .build();
                    return serverRepository.save(newServer);
                });

        MetricObservation observation = MetricObservation.builder()
                .server(server)
                .timestamp(request.getTimestamp())
                .cpuPercentTotal(request.getCpu().getPercentTotal())
                .cpuPercentUser(request.getCpu().getPercentUser())
                .cpuPercentSystem(request.getCpu().getPercentSystem())
                .cpuPercentIdle(request.getCpu().getPercentIdle())
                .cpuPercentIowait(request.getCpu().getPercentIowait())
                .loadAverage1m(request.getCpu().getLoadAverage1m())
                .loadAverage5m(request.getCpu().getLoadAverage5m())
                .loadAverage15m(request.getCpu().getLoadAverage15m())
                .numCpuCores(request.getCpu().getNumCpuCores())
                .numLogicalCpus(request.getCpu().getNumLogicalCpus())
                .memoryTotalBytes(request.getMemory().getTotalBytes())
                .memoryAvailableBytes(request.getMemory().getAvailableBytes())
                .memoryUsedBytes(request.getMemory().getUsedBytes())
                .memoryFreeBytes(request.getMemory().getFreeBytes())
                .memoryActiveBytes(request.getMemory().getActiveBytes())
                .memoryInactiveBytes(request.getMemory().getInactiveBytes())
                .memoryBuffersBytes(request.getMemory().getBuffersBytes())
                .memoryCachedBytes(request.getMemory().getCachedBytes())
                .swapTotalBytes(request.getMemory().getSwapTotalBytes())
                .swapUsedBytes(request.getMemory().getSwapUsedBytes())
                .swapFreeBytes(request.getMemory().getSwapFreeBytes())
                .swapPercentUsed(request.getMemory().getSwapPercentUsed())
                .diskTotalBytes(request.getDisk().getTotalBytes())
                .diskUsedBytes(request.getDisk().getUsedBytes())
                .diskFreeBytes(request.getDisk().getFreeBytes())
                .diskPercentUsed(request.getDisk().getPercentUsed())
                .diskIOReadBytes(request.getDisk().getIoReadBytes())
                .diskIOWriteBytes(request.getDisk().getIoWriteBytes())
                .diskIOReadCount(request.getDisk().getIoReadCount())
                .diskIOWriteCount(request.getDisk().getIoWriteCount())
                .networkBytesSent(request.getNetwork().getBytesSent())
                .networkBytesRecv(request.getNetwork().getBytesRecv())
                .networkPacketsSent(request.getNetwork().getPacketsSent())
                .networkPacketsRecv(request.getNetwork().getPacketsRecv())
                .networkErrin(request.getNetwork().getErrin())
                .networkErrout(request.getNetwork().getErrout())
                .networkDropin(request.getNetwork().getDropin())
                .networkDropout(request.getNetwork().getDropout())
                .processCount(request.getProcess() != null ? request.getProcess().getProcessCount() : 0)
                .uptimeSeconds(request.getSystem() != null ? request.getSystem().getUptimeSeconds() : 0.0)
                .bootTime(request.getSystem() != null ? request.getSystem().getBootTime() : Instant.now())
                .createdAt(Instant.now())
                .build();

        return metricObservationRepository.save(observation);
    }

    @Transactional(readOnly = true)
    public MetricObservation getLatestObservation(Long serverId) {
        return metricObservationRepository.findRecentByServerId(serverId, 1).stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("No metrics found for server id " + serverId));
    }
}