package com.infrapulse.controller;

import com.infrapulse.dto.TroubleshootingPlaybookDto;
import com.infrapulse.service.TroubleshootingPlaybookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playbooks")
public class TroubleshootingPlaybookController {

    private final TroubleshootingPlaybookService playbookService;

    public TroubleshootingPlaybookController(TroubleshootingPlaybookService playbookService) {
        this.playbookService = playbookService;
    }

    @GetMapping
    public ResponseEntity<List<TroubleshootingPlaybookDto>> getAllPlaybooks() {
        return ResponseEntity.ok(playbookService.getAllPlaybooks());
    }

    @GetMapping("/{conditionCode}")
    public ResponseEntity<TroubleshootingPlaybookDto> getPlaybookByConditionCode(
            @PathVariable String conditionCode) {
        return ResponseEntity.ok(playbookService.getPlaybookByConditionCode(conditionCode));
    }
}
