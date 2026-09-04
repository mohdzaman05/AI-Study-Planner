package com.studyplanner.controller;

import com.studyplanner.dto.StudyTaskResponse;
import com.studyplanner.dto.TaskStatusUpdateRequest;
import com.studyplanner.model.TaskStatus;
import com.studyplanner.security.UserPrincipal;
import com.studyplanner.service.StudyTaskService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class StudyTaskController {

    private final StudyTaskService studyTaskService;

    public StudyTaskController(StudyTaskService studyTaskService) {
        this.studyTaskService = studyTaskService;
    }

    @GetMapping
    public ResponseEntity<List<StudyTaskResponse>> getTasks(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) TaskStatus status,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<StudyTaskResponse> tasks = studyTaskService.getTasks(userPrincipal.getId(), date, status);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/today")
    public ResponseEntity<List<StudyTaskResponse>> getTodayTasks(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<StudyTaskResponse> tasks = studyTaskService.getTodayTasks(userPrincipal.getId());
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<StudyTaskResponse> completeTask(@PathVariable Long id,
                                                          @AuthenticationPrincipal UserPrincipal userPrincipal) {
        StudyTaskResponse updated = studyTaskService.completeTask(id, userPrincipal.getId());
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/missed")
    public ResponseEntity<StudyTaskResponse> markTaskMissed(@PathVariable Long id,
                                                            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        StudyTaskResponse updated = studyTaskService.markTaskMissed(id, userPrincipal.getId());
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<StudyTaskResponse> updateTaskStatus(@PathVariable Long id,
                                                              @Valid @RequestBody TaskStatusUpdateRequest request,
                                                              @AuthenticationPrincipal UserPrincipal userPrincipal) {
        StudyTaskResponse updated = studyTaskService.updateTaskStatus(id, request.getStatus(), userPrincipal.getId());
        return ResponseEntity.ok(updated);
    }
}
