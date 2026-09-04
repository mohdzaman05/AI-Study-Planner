package com.studyplanner.controller;

import com.studyplanner.dto.ProgressOverviewResponse;
import com.studyplanner.security.UserPrincipal;
import com.studyplanner.service.ProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @GetMapping
    public ResponseEntity<ProgressOverviewResponse> getProgress(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        ProgressOverviewResponse overview = progressService.getProgressOverview(userPrincipal.getId());
        return ResponseEntity.ok(overview);
    }
}
