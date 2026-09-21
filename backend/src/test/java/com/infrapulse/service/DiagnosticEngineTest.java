package com.infrapulse.service;

import com.infrapulse.model.DiagnosticResult;
import com.infrapulse.model.MetricObservation;
import com.infrapulse.model.Server;
import com.infrapulse.repository.DiagnosticResultRepository;
import com.infrapulse.repository.MetricObservationRepository;
import com.infrapulse.repository.ServerRepository;
import com.infrapulse.repository.TroubleshootingPlaybookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiagnosticEngineTest {

    @Mock
    private MetricObservationRepository metricObservationRepository;

    @Mock
    private DiagnosticResultRepository diagnosticResultRepository;

    @Mock
    private ServerRepository serverRepository;

    @Mock
    private TroubleshootingPlaybookRepository playbookRepository;

    private DiagnosticEngine diagnosticEngine;

    @BeforeEach
    void setUp() {
        diagnosticEngine = new DiagnosticEngine(
                metricObservationRepository,
                diagnosticResultRepository,
                serverRepository,
                playbookRepository);
    }

    @Test
    void evaluateDetectsCpuAboveThreshold() {
        List<DiagnosticResult> results = evaluate(healthyObservation().cpuPercentTotal(90.1).build());

        assertThat(results).singleElement().satisfies(result -> {
            assertThat(result.getConditionCode()).isEqualTo("HIGH_CPU");
            assertThat(result.getDiagnosis()).isEqualTo("High CPU utilization");
            assertThat(result.getEvidence()).contains("90.1%");
        });
    }

    @Test
    void evaluateDetectsMemoryPressureWhenMemoryAndSwapExceedThresholds() {
        List<DiagnosticResult> results = evaluate(healthyObservation()
                .memoryTotalBytes(100)
                .memoryAvailableBytes(9)
                .swapPercentUsed(50.1)
                .build());

        assertThat(results).singleElement().satisfies(result -> {
            assertThat(result.getConditionCode()).isEqualTo("MEMORY_PRESSURE");
            assertThat(result.getDiagnosis()).isEqualTo("Memory pressure");
            assertThat(result.getEvidence()).contains("91.0% used");
        });
    }

    @Test
    void evaluateDoesNotDetectMemoryPressureWithoutSwapPressure() {
        List<DiagnosticResult> results = evaluate(healthyObservation()
                .memoryTotalBytes(100)
                .memoryAvailableBytes(1)
                .swapPercentUsed(50.0)
                .build());

        assertThat(results).isEmpty();
        verifyNoInteractions(diagnosticResultRepository);
    }

    @Test
    void evaluateDetectsDiskCapacityAboveThreshold() {
        List<DiagnosticResult> results = evaluate(healthyObservation().diskPercentUsed(90.1).build());

        assertThat(results).singleElement().satisfies(result -> {
            assertThat(result.getConditionCode()).isEqualTo("DISK_CAPACITY");
            assertThat(result.getDiagnosis()).isEqualTo("Low disk capacity");
        });
    }

    @Test
    void evaluateDetectsNetworkDegradationFromErrorsOrDrops() {
        List<DiagnosticResult> results = evaluate(healthyObservation().networkDropout(1).build());

        assertThat(results).singleElement().satisfies(result -> {
            assertThat(result.getConditionCode()).isEqualTo("NETWORK_DEGRADATION");
            assertThat(result.getDiagnosis()).isEqualTo("Network connectivity degradation");
        });
    }

    @Test
    void evaluateReturnsNoDiagnosticsForHealthyObservation() {
        List<DiagnosticResult> results = evaluate(healthyObservation().build());

        assertThat(results).isEmpty();
        verifyNoInteractions(diagnosticResultRepository);
    }

    @Test
    void evaluateDoesNotTriggerAtExactThresholdBoundaries() {
        List<DiagnosticResult> results = evaluate(healthyObservation()
                .cpuPercentTotal(90.0)
                .memoryTotalBytes(100)
                .memoryAvailableBytes(10)
                .swapPercentUsed(50.0)
                .diskPercentUsed(90.0)
                .networkErrin(0)
                .networkErrout(0)
                .networkDropin(0)
                .networkDropout(0)
                .build());

        assertThat(results).isEmpty();
        verifyNoInteractions(diagnosticResultRepository);
    }

    @Test
    void evaluateRejectsServerWithoutMetrics() {
        when(metricObservationRepository.findRecentByServerId(99L, 1)).thenReturn(List.of());

        assertThatThrownBy(() -> diagnosticEngine.evaluate(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No metrics found for server id: 99");
        verifyNoInteractions(serverRepository, diagnosticResultRepository);
    }

    private List<DiagnosticResult> evaluate(MetricObservation observation) {
        Server server = Server.builder().id(7L).hostname("server-1").build();
        when(metricObservationRepository.findRecentByServerId(7L, 1)).thenReturn(List.of(observation));
        when(serverRepository.findById(7L)).thenReturn(Optional.of(server));

        List<DiagnosticResult> results = diagnosticEngine.evaluate(7L);

        assertThat(results).allSatisfy(result -> {
            assertThat(result.getServer()).isSameAs(server);
            assertThat(result.getMetricObservation()).isSameAs(observation);
            assertThat(result.getTimestamp()).isNotNull();
            assertThat(result.isActive()).isTrue();
        });
        verify(diagnosticResultRepository, times(results.size())).save(any(DiagnosticResult.class));
        return results;
    }

    private MetricObservation.MetricObservationBuilder healthyObservation() {
        return MetricObservation.builder()
                .cpuPercentTotal(50.0)
                .memoryTotalBytes(100)
                .memoryAvailableBytes(20)
                .swapPercentUsed(10.0)
                .diskPercentUsed(50.0)
                .networkErrin(0)
                .networkErrout(0)
                .networkDropin(0)
                .networkDropout(0);
    }
}
