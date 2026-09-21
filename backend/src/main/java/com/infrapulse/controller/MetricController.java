package com.infrapulse.controller;

import com.infrapulse.dto.MetricIngestionRequest;
import com.infrapulse.dto.MetricResponse;
import com.infrapulse.model.MetricObservation;
import com.infrapulse.service.MetricIngestionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/metrics")
public class MetricController {

    private final MetricIngestionService metricIngestionService;

    public MetricController(MetricIngestionService metricIngestionService) {
        this.metricIngestionService = metricIngestionService;
    }

    @PostMapping
    public ResponseEntity<MetricResponse> ingestMetrics(@Valid @RequestBody MetricIngestionRequest request) {
        MetricObservation observation = metricIngestionService.ingest(request);
        MetricResponse response = new MetricResponse();
        response.setId(observation.getId());
        response.setHostname(request.getHostname());
        response.setTimestamp(observation.getTimestamp());
        response.setCpuPercentTotal(observation.getCpuPercentTotal());
        response.setMemoryPercentUsed(observation.getMemoryUsedBytes() * 100.0 / observation.getMemoryTotalBytes());
        response.setDiskPercentUsed(observation.getDiskPercentUsed());
        response.setNetworkErrors((int)(observation.getNetworkErrin() + observation.getNetworkErrout()
                + observation.getNetworkDropin() + observation.getNetworkDropout()));
        response.setProcessCount(observation.getProcessCount());
        response.setUptimeSeconds(observation.getUptimeSeconds());
        return ResponseEntity.ok(response);
    }
}