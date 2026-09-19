package com.infrapulse.service;

import com.infrapulse.dto.IncidentStatusTransition;
import com.infrapulse.dto.IncidentDto;
import com.infrapulse.model.Incident;
import com.infrapulse.model.Server;
import com.infrapulse.repository.EscalationRepository;
import com.infrapulse.repository.IncidentRepository;
import com.infrapulse.repository.IncidentStatusHistoryRepository;
import com.infrapulse.repository.ServerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private IncidentStatusHistoryRepository statusHistoryRepository;

    @Mock
    private EscalationRepository escalationRepository;

    @Mock
    private ServerRepository serverRepository;

    private IncidentService incidentService;

    @BeforeEach
    void setUp() {
        incidentService = new IncidentService(
                incidentRepository,
                statusHistoryRepository,
                escalationRepository,
                serverRepository);
    }

    @Test
    void updateStatusAdvancesIncidentThroughValidTransition() {
        Incident incident = incident("NEW");
        when(incidentRepository.findById(4L)).thenReturn(Optional.of(incident));
        when(incidentRepository.save(incident)).thenReturn(incident);
        IncidentStatusTransition transition = new IncidentStatusTransition(
                "ACKNOWLEDGED", "Technician acknowledged the alert");

        IncidentDto result = incidentService.updateStatus(4L, transition);

        assertThat(result.getStatus()).isEqualTo("ACKNOWLEDGED");
        assertThat(incident.getStatusHistory()).containsExactly("NEW", "ACKNOWLEDGED");
        verify(statusHistoryRepository).save(any());
        verify(incidentRepository).save(incident);
    }

    @Test
    void updateStatusRejectsInvalidTransition() {
        Incident incident = incident("NEW");
        when(incidentRepository.findById(4L)).thenReturn(Optional.of(incident));
        IncidentStatusTransition transition = new IncidentStatusTransition("RESOLVED", null);

        assertThatThrownBy(() -> incidentService.updateStatus(4L, transition))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid status transition");
        verify(incidentRepository, never()).save(any());
        verifyNoInteractions(statusHistoryRepository);
    }

    @Test
    void updateStatusRejectsMissingIncident() {
        when(incidentRepository.findById(404L)).thenReturn(Optional.empty());
        IncidentStatusTransition transition = new IncidentStatusTransition("ACKNOWLEDGED", null);

        assertThatThrownBy(() -> incidentService.updateStatus(404L, transition))
                .isInstanceOf(jakarta.persistence.EntityNotFoundException.class)
                .hasMessage("Incident not found with id: 404");
    }

    private Incident incident(String status) {
        return Incident.builder()
                .id(4L)
                .server(Server.builder().id(1L).hostname("server-1").build())
                .incidentNumber("INC-4")
                .status(status)
                .severity("HIGH")
                .symptom("High CPU")
                .detectedMetric("cpu_percent_total")
                .probableCause("Runaway process")
                .evidence("CPU exceeded threshold")
                .recommendedAction("Inspect processes")
                .statusHistory(new ArrayList<>(java.util.List.of(status)))
                .troubleshootingHistory(new ArrayList<>())
                .evidenceLog(new ArrayList<>())
                .build();
    }
}
