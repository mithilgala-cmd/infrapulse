package com.infrapulse.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetricIngestionRequest {

    @NotBlank
    private String hostname;

    @NotNull
    private Instant timestamp;

    @NotNull
    private CpuData cpu;

    @NotNull
    private MemoryData memory;

    @NotNull
    private DiskData disk;

    @NotNull
    private NetworkData network;

    private ProcessData process;

    private SystemData system;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CpuData {
        @NotNull
        private Double percentTotal;
        @NotNull
        private Double percentUser;
        @NotNull
        private Double percentSystem;
        @NotNull
        private Double percentIdle;
        @NotNull
        private Double percentIowait;
        @NotNull
        private Double loadAverage1m;
        @NotNull
        private Double loadAverage5m;
        @NotNull
        private Double loadAverage15m;
        @NotNull
        private Integer numCpuCores;
        @NotNull
        private Integer numLogicalCpus;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemoryData {
        @NotNull
        private Long totalBytes;
        @NotNull
        private Long availableBytes;
        @NotNull
        private Long usedBytes;
        @NotNull
        private Long freeBytes;
        @NotNull
        private Long activeBytes;
        @NotNull
        private Long inactiveBytes;
        @NotNull
        private Long buffersBytes;
        @NotNull
        private Long cachedBytes;
        @NotNull
        private Long swapTotalBytes;
        @NotNull
        private Long swapUsedBytes;
        @NotNull
        private Long swapFreeBytes;
        @NotNull
        private Double swapPercentUsed;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiskData {
        @NotNull
        private Long totalBytes;
        @NotNull
        private Long usedBytes;
        @NotNull
        private Long freeBytes;
        @NotNull
        private Double percentUsed;
        @NotNull
        private Long ioReadBytes;
        @NotNull
        private Long ioWriteBytes;
        @NotNull
        private Long ioReadCount;
        @NotNull
        private Long ioWriteCount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NetworkData {
        @NotNull
        private Long bytesSent;
        @NotNull
        private Long bytesRecv;
        @NotNull
        private Long packetsSent;
        @NotNull
        private Long packetsRecv;
        @NotNull
        private Long errin;
        @NotNull
        private Long errout;
        @NotNull
        private Long dropin;
        @NotNull
        private Long dropout;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessData {
        private Integer processCount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemData {
        @NotNull
        private Double uptimeSeconds;
        @NotNull
        private Instant bootTime;
    }
}