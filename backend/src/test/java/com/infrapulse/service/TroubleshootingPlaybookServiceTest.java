package com.infrapulse.service;

import com.infrapulse.dto.TroubleshootingPlaybookDto;
import com.infrapulse.model.TroubleshootingPlaybook;
import com.infrapulse.repository.TroubleshootingPlaybookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TroubleshootingPlaybookServiceTest {

    @Mock
    private TroubleshootingPlaybookRepository playbookRepository;

    private TroubleshootingPlaybookService playbookService;

    @BeforeEach
    void setUp() {
        playbookService = new TroubleshootingPlaybookService(playbookRepository);
    }

    @Test
    void getAllPlaybooksReturnsPlaybookDtosInRepositoryOrder() {
        when(playbookRepository.findAllByOrderByConditionCodeAsc())
                .thenReturn(List.of(playbook("DISK_CAPACITY"), playbook("HIGH_CPU")));

        List<TroubleshootingPlaybookDto> playbooks = playbookService.getAllPlaybooks();

        assertThat(playbooks).extracting(TroubleshootingPlaybookDto::getConditionCode)
                .containsExactly("DISK_CAPACITY", "HIGH_CPU");
        assertThat(playbooks.getFirst().getChecks()).contains("Check filesystem usage");
    }

    @Test
    void getPlaybookByConditionCodeReturnsMatchingPlaybook() {
        when(playbookRepository.findByConditionCode("HIGH_CPU"))
                .thenReturn(Optional.of(playbook("HIGH_CPU")));

        TroubleshootingPlaybookDto playbook = playbookService.getPlaybookByConditionCode("HIGH_CPU");

        assertThat(playbook.getConditionCode()).isEqualTo("HIGH_CPU");
        assertThat(playbook.getSymptoms()).contains("CPU usage is above 90%");
        assertThat(playbook.getRecommendedActions()).contains("Inspect top processes");
    }

    @Test
    void getPlaybookByConditionCodeRejectsUnknownConditionCode() {
        when(playbookRepository.findByConditionCode("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> playbookService.getPlaybookByConditionCode("UNKNOWN"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Troubleshooting playbook not found for condition code: UNKNOWN");
    }

    private TroubleshootingPlaybook playbook(String conditionCode) {
        return TroubleshootingPlaybook.builder()
                .id(10L)
                .conditionCode(conditionCode)
                .title(conditionCode + " Playbook")
                .symptoms(List.of(conditionCode.equals("HIGH_CPU")
                        ? "CPU usage is above 90%"
                        : "Disk utilization is above 90%"))
                .checks(List.of(conditionCode.equals("HIGH_CPU")
                        ? "Identify top CPU-consuming processes"
                        : "Check filesystem usage"))
                .possibleCauses(List.of("Runaway process"))
                .recommendedActions(List.of(conditionCode.equals("HIGH_CPU")
                        ? "Inspect top processes"
                        : "Remove only verified temporary data"))
                .escalationConditions(List.of("Condition persists after mitigation"))
                .build();
    }
}
