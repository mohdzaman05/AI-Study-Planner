package com.studyplanner.controller;

import com.studyplanner.dto.*;
import com.studyplanner.model.Exam;
import com.studyplanner.model.Subject;
import com.studyplanner.repository.ExamRepository;
import com.studyplanner.repository.SubjectRepository;
import com.studyplanner.security.UserPrincipal;
import com.studyplanner.service.AiService;
import com.studyplanner.service.StudyPlanService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;
    private final StudyPlanService studyPlanService;
    private final SubjectRepository subjectRepository;
    private final ExamRepository examRepository;

    public AiController(AiService aiService,
                        StudyPlanService studyPlanService,
                        SubjectRepository subjectRepository,
                        ExamRepository examRepository) {
        this.aiService = aiService;
        this.studyPlanService = studyPlanService;
        this.subjectRepository = subjectRepository;
        this.examRepository = examRepository;
    }

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(@Valid @RequestBody AiChatRequest request,
                                               @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<Subject> subjects = subjectRepository.findByUserIdOrderByNameAsc(userPrincipal.getId());
        List<Exam> exams = examRepository.findByUserIdOrderByExamDateAsc(userPrincipal.getId());

        String reply = aiService.chatWithAi(request.getMessage(), userPrincipal.getId(), subjects, exams);
        return ResponseEntity.ok(new AiChatResponse(reply));
    }

    @PostMapping("/generate-plan")
    public ResponseEntity<StudyPlanResponse> generatePlan(@Valid @RequestBody GeneratePlanRequest request,
                                                          @AuthenticationPrincipal UserPrincipal userPrincipal) {
        StudyPlanResponse response = studyPlanService.generatePlan(request, userPrincipal.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/adjust-plan")
    public ResponseEntity<StudyPlanResponse> adjustActivePlan(@RequestBody AdjustPlanRequest request,
                                                              @AuthenticationPrincipal UserPrincipal userPrincipal) {
        StudyPlanResponse activePlan = studyPlanService.getActivePlan(userPrincipal.getId())
                .orElseThrow(() -> new com.studyplanner.exception.ResourceNotFoundException("No active study plan found to adjust"));

        StudyPlanResponse updated = studyPlanService.adjustPlan(activePlan.getId(), request, userPrincipal.getId());
        return ResponseEntity.ok(updated);
    }
}
