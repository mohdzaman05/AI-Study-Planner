package com.studyplanner.controller;

import com.studyplanner.dto.AdjustPlanRequest;
import com.studyplanner.dto.GeneratePlanRequest;
import com.studyplanner.dto.StudyPlanResponse;
import com.studyplanner.security.UserPrincipal;
import com.studyplanner.service.StudyPlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/study-plans")
public class StudyPlanController {

    private final StudyPlanService studyPlanService;

    public StudyPlanController(StudyPlanService studyPlanService) {
        this.studyPlanService = studyPlanService;
    }

    @PostMapping("/generate")
    public ResponseEntity<StudyPlanResponse> generatePlan(@Valid @RequestBody GeneratePlanRequest request,
                                                          @AuthenticationPrincipal UserPrincipal userPrincipal) {
        StudyPlanResponse response = studyPlanService.generatePlan(request, userPrincipal.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<StudyPlanResponse>> getAllPlans(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<StudyPlanResponse> plans = studyPlanService.getAllPlans(userPrincipal.getId());
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/active")
    public ResponseEntity<StudyPlanResponse> getActivePlan(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return studyPlanService.getActivePlan(userPrincipal.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudyPlanResponse> getPlanById(@PathVariable Long id,
                                                         @AuthenticationPrincipal UserPrincipal userPrincipal) {
        StudyPlanResponse plan = studyPlanService.getPlanById(id, userPrincipal.getId());
        return ResponseEntity.ok(plan);
    }

    @PostMapping("/{id}/adjust")
    public ResponseEntity<StudyPlanResponse> adjustPlan(@PathVariable Long id,
                                                        @RequestBody AdjustPlanRequest request,
                                                        @AuthenticationPrincipal UserPrincipal userPrincipal) {
        StudyPlanResponse updated = studyPlanService.adjustPlan(id, request, userPrincipal.getId());
        return ResponseEntity.ok(updated);
    }
}
