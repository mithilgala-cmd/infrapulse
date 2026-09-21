package com.infrapulse.service;

import com.infrapulse.dto.TroubleshootingPlaybookDto;
import com.infrapulse.repository.TroubleshootingPlaybookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TroubleshootingPlaybookService {

    private final TroubleshootingPlaybookRepository playbookRepository;

    public TroubleshootingPlaybookService(TroubleshootingPlaybookRepository playbookRepository) {
        this.playbookRepository = playbookRepository;
    }

    public List<TroubleshootingPlaybookDto> getAllPlaybooks() {
        return playbookRepository.findAllByOrderByConditionCodeAsc().stream()
                .map(TroubleshootingPlaybookDto::from)
                .toList();
    }

    public TroubleshootingPlaybookDto getPlaybookByConditionCode(String conditionCode) {
        return playbookRepository.findByConditionCode(conditionCode)
                .map(TroubleshootingPlaybookDto::from)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Troubleshooting playbook not found for condition code: " + conditionCode));
    }
}
