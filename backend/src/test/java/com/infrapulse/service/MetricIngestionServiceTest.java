package com.infrapulse.service;

import com.infrapulse.dto.MetricIngestionRequest;
import com.infrapulse.model.MetricObservation;
import com.infrapulse.model.Server;
import com.infrapulse.repository.MetricObservationRepository;
import com.infrapulse.repository.ServerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetricIngestionServiceTest {

    @Mock
    private ServerRepository serverRepository;

    @Mock
    private MetricObservationRepository metricObservationRepository;

    private MetricIngestionService metricIngestionService;
    private Validator validator;

    @BeforeEach
    void setUp() {
        metricIngestionService = new MetricIngestionService(serverRepository, metricObservationRepository);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void ingestCreatesServerWhenHostnameIsNewAndPersistsObservation() {
        MetricIngestionRequest request = sampleRequest("server-1");
        Server savedServer = Server.builder().id(1L).hostname("server-1").build();
        when(serverRepository.findByHostname("server-1")).thenReturn(Optional.empty());
        when(serverRepository.save(any(Server.class))).thenReturn(savedServer);
        when(metricObservationRepository.save(any(MetricObservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MetricObservation observation = metricIngestionService.ingest(request);

        ArgumentCaptor<Server> serverCaptor = ArgumentCaptor.forClass(Server.class);
        verify(serverRepository).save(serverCaptor.capture());
        assertThat(serverCaptor.getValue().getHostname()).isEqualTo("server-1");
        assertThat(serverCaptor.getValue().getAgentId()).isEqualTo("agent-server-1");

        assertThat(observation.getServer()).isSameAs(savedServer);
        assertThat(observation.getTimestamp()).isEqualTo(request.getTimestamp());
        assertThat(observation.getCpuPercentTotal()).isEqualTo(42.5);
        assertThat(observation.getMemoryTotalBytes()).isEqualTo(1000L);
        assertThat(observation.getDiskPercentUsed()).isEqualTo(55.0);
        assertThat(observation.getNetworkErrin()).isEqualTo(1L);
        assertThat(observation.getProcessCount()).isEqualTo(123);
        assertThat(observation.getUptimeSeconds()).isEqualTo(3600.0);
        assertThat(observation.getCreatedAt()).isNotNull();
    }

    @Test
    void ingestReusesExistingServerAndDefaultsOptionalProcessAndSystemData() {
        MetricIngestionRequest request = sampleRequest("server-2");
        request.setProcess(null);
        request.setSystem(null);
        Server existingServer = Server.builder().id(2L).hostname("server-2").build();
        when(serverRepository.findByHostname("server-2")).thenReturn(Optional.of(existingServer));
        when(metricObservationRepository.save(any(MetricObservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MetricObservation observation = metricIngestionService.ingest(request);

        verify(serverRepository, never()).save(any(Server.class));
        assertThat(observation.getServer()).isSameAs(existingServer);
        assertThat(observation.getProcessCount()).isZero();
        assertThat(observation.getUptimeSeconds()).isZero();
        assertThat(observation.getBootTime()).isNotNull();
    }

    @Test
    void getLatestObservationReturnsMostRecentMetricForServer() {
        MetricObservation latest = MetricObservation.builder().id(8L).build();
        when(metricObservationRepository.findRecentByServerId(2L, 1)).thenReturn(java.util.List.of(latest));

        MetricObservation result = metricIngestionService.getLatestObservation(2L);

        assertThat(result).isSameAs(latest);
    }

    @Test
    void getLatestObservationRejectsServerWithoutMetrics() {
        when(metricObservationRepository.findRecentByServerId(2L, 1)).thenReturn(java.util.List.of());

        assertThatThrownBy(() -> metricIngestionService.getLatestObservation(2L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("No metrics found for server id 2");
    }

    @Test
    void validationRejectsMissingRequiredMetricPayloadSections() {
        MetricIngestionRequest request = sampleRequest("");
        request.setCpu(null);
        request.setNetwork(null);

        Set<ConstraintViolation<MetricIngestionRequest>> violations = validator.validate(request);

        assertThat(violations).extracting(violation -> violation.getPropertyPath().toString())
                .contains("hostname", "cpu", "network");
    }

    private MetricIngestionRequest sampleRequest(String hostname) {
        Instant timestamp = Instant.parse("2026-09-21T12:00:00Z");
        return MetricIngestionRequest.builder()
                .hostname(hostname)
                .timestamp(timestamp)
                .cpu(new MetricIngestionRequest.CpuData(
                        42.5, 20.0, 10.0, 57.5, 1.0,
                        0.5, 0.4, 0.3, 4, 8))
                .memory(new MetricIngestionRequest.MemoryData(
                        1000L, 400L, 600L, 300L, 100L, 50L,
                        25L, 125L, 200L, 25L, 175L, 12.5))
                .disk(new MetricIngestionRequest.DiskData(
                        2000L, 1100L, 900L, 55.0, 100L, 200L, 3L, 4L))
                .network(new MetricIngestionRequest.NetworkData(
                        1000L, 2000L, 10L, 20L, 1L, 0L, 0L, 0L))
                .process(new MetricIngestionRequest.ProcessData(123))
                .system(new MetricIngestionRequest.SystemData(3600.0, timestamp.minusSeconds(3600)))
                .build();
    }
}
