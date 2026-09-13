package com.infrapulse.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetricResponse {

    private Long id;
    private String hostname;
    private Instant timestamp;
    private double cpuPercentTotal;
    private double memoryPercentUsed;
    private double diskPercentUsed;
    private int networkErrors;
    private int processCount;
    private double uptimeSeconds;

    public static MetricResponse fromMetricObservation(Object[] fields) {
        MetricResponse response = new MetricResponse();
        response.setId((Long) fields[0]);
        response.setHostname((String) fields[1]);
        response.setTimestamp((Instant) fields[2]);
        response.setCpuPercentTotal((Double) fields[3]);
        response.setMemoryPercentUsed(((Number) fields[4]).doubleValue());
        response.setDiskPercentUsed(((Number) fields[5]).doubleValue());
        response.setNetworkErrors(((Number) fields[6]).intValue());
        response.setProcessCount(((Number) fields[7]).intValue());
        response.setUptimeSeconds(((Number) fields[8]).doubleValue());
        return response;
    }
}