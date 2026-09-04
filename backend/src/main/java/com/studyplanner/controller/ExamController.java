package com.studyplanner.controller;

import com.studyplanner.dto.ExamRequest;
import com.studyplanner.dto.ExamResponse;
import com.studyplanner.security.UserPrincipal;
import com.studyplanner.service.ExamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping
    public ResponseEntity<List<ExamResponse>> getExams(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<ExamResponse> exams = examService.getExams(userPrincipal.getId());
        return ResponseEntity.ok(exams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamResponse> getExamById(@PathVariable Long id,
                                                    @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ExamResponse exam = examService.getExamById(id, userPrincipal.getId());
        return ResponseEntity.ok(exam);
    }

    @PostMapping
    public ResponseEntity<ExamResponse> createExam(@Valid @RequestBody ExamRequest request,
                                                   @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ExamResponse created = examService.createExam(request, userPrincipal.getId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamResponse> updateExam(@PathVariable Long id,
                                                   @Valid @RequestBody ExamRequest request,
                                                   @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ExamResponse updated = examService.updateExam(id, request, userPrincipal.getId());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(@PathVariable Long id,
                                           @AuthenticationPrincipal UserPrincipal userPrincipal) {
        examService.deleteExam(id, userPrincipal.getId());
        return ResponseEntity.noContent().build();
    }
}
