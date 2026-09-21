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
    void evaluateCreatesDiagnosticsForEachTriggeredCondition() {
        MetricObservation observation = MetricObservation.builder()
                .cpuPercentTotal(95.0)
                .memoryTotalBytes(100)
                .memoryAvailableBytes(5)
                .swapPercentUsed(60.0)
                .diskPercentUsed(95.0)
                .networkErrin(1)
                .networkErrout(0)
                .networkDropin(0)
                .networkDropout(0)
                .build();
        Server server = Server.builder().id(7L).hostname("server-1").build();
        when(metricObservationRepository.findRecentByServerId(7L, 1)).thenReturn(List.of(observation));
        when(serverRepository.findById(7L)).thenReturn(Optional.of(server));
        when(diagnosticResultRepository.save(any(DiagnosticResult.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<DiagnosticResult> results = diagnosticEngine.evaluate(7L);

        assertThat(results).extracting(DiagnosticResult::getConditionCode)
                .containsExactlyInAnyOrder("HIGH_CPU", "MEMORY_PRESSURE", "DISK_CAPACITY", "NETWORK_DEGRADATION");
        assertThat(results).allSatisfy(result -> {
            assertThat(result.getServer()).isSameAs(server);
            assertThat(result.getMetricObservation()).isSameAs(observation);
            assertThat(result.getTimestamp()).isNotNull();
            assertThat(result.isActive()).isTrue();
        });
        verify(diagnosticResultRepository, times(4)).save(any(DiagnosticResult.class));
    }

    @Test
    void evaluateReturnsNoDiagnosticsForHealthyObservation() {
        MetricObservation observation = MetricObservation.builder()
                .cpuPercentTotal(50.0)
                .memoryTotalBytes(100)
                .memoryAvailableBytes(20)
                .swapPercentUsed(10.0)
                .diskPercentUsed(50.0)
                .build();
        when(metricObservationRepository.findRecentByServerId(2L, 1)).thenReturn(List.of(observation));
        when(serverRepository.findById(2L)).thenReturn(Optional.of(Server.builder().id(2L).build()));

        List<DiagnosticResult> results = diagnosticEngine.evaluate(2L);

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
}
