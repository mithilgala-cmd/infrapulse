package com.infrapulse.dto;

import com.infrapulse.model.TroubleshootingPlaybook;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TroubleshootingPlaybookDto {

    private Long id;
    private String conditionCode;
    private String title;
    private List<String> symptoms;
    private List<String> checks;
    private List<String> possibleCauses;
    private List<String> recommendedActions;
    private List<String> escalationConditions;

    public static TroubleshootingPlaybookDto from(TroubleshootingPlaybook playbook) {
        return TroubleshootingPlaybookDto.builder()
                .id(playbook.getId())
                .conditionCode(playbook.getConditionCode())
                .title(playbook.getTitle())
                .symptoms(playbook.getSymptoms())
                .checks(playbook.getChecks())
                .possibleCauses(playbook.getPossibleCauses())
                .recommendedActions(playbook.getRecommendedActions())
                .escalationConditions(playbook.getEscalationConditions())
                .build();
    }
}
