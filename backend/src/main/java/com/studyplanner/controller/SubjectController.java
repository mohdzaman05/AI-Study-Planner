package com.studyplanner.controller;

import com.studyplanner.dto.SubjectRequest;
import com.studyplanner.dto.SubjectResponse;
import com.studyplanner.security.UserPrincipal;
import com.studyplanner.service.SubjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    public ResponseEntity<List<SubjectResponse>> getAllSubjects(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<SubjectResponse> subjects = subjectService.getAllSubjects(userPrincipal.getId());
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponse> getSubjectById(@PathVariable Long id,
                                                          @AuthenticationPrincipal UserPrincipal userPrincipal) {
        SubjectResponse subject = subjectService.getSubjectById(id, userPrincipal.getId());
        return ResponseEntity.ok(subject);
    }

    @PostMapping
    public ResponseEntity<SubjectResponse> createSubject(@Valid @RequestBody SubjectRequest request,
                                                         @AuthenticationPrincipal UserPrincipal userPrincipal) {
        SubjectResponse created = subjectService.createSubject(request, userPrincipal.getId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubjectResponse> updateSubject(@PathVariable Long id,
                                                         @Valid @RequestBody SubjectRequest request,
                                                         @AuthenticationPrincipal UserPrincipal userPrincipal) {
        SubjectResponse updated = subjectService.updateSubject(id, request, userPrincipal.getId());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id,
                                              @AuthenticationPrincipal UserPrincipal userPrincipal) {
        subjectService.deleteSubject(id, userPrincipal.getId());
        return ResponseEntity.noContent().build();
    }
}
