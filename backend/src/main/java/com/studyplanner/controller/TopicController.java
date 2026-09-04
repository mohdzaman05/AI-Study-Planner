package com.studyplanner.controller;

import com.studyplanner.dto.TopicRequest;
import com.studyplanner.dto.TopicResponse;
import com.studyplanner.model.TopicStatus;
import com.studyplanner.security.UserPrincipal;
import com.studyplanner.service.TopicService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping("/subjects/{subjectId}/topics")
    public ResponseEntity<List<TopicResponse>> getTopicsBySubject(@PathVariable Long subjectId,
                                                                  @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<TopicResponse> topics = topicService.getTopicsBySubject(subjectId, userPrincipal.getId());
        return ResponseEntity.ok(topics);
    }

    @PostMapping("/subjects/{subjectId}/topics")
    public ResponseEntity<TopicResponse> createTopic(@PathVariable Long subjectId,
                                                     @Valid @RequestBody TopicRequest request,
                                                     @AuthenticationPrincipal UserPrincipal userPrincipal) {
        TopicResponse created = topicService.createTopic(subjectId, request, userPrincipal.getId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/topics/{id}")
    public ResponseEntity<TopicResponse> updateTopic(@PathVariable Long id,
                                                     @Valid @RequestBody TopicRequest request,
                                                     @AuthenticationPrincipal UserPrincipal userPrincipal) {
        TopicResponse updated = topicService.updateTopic(id, request, userPrincipal.getId());
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/topics/{id}/status")
    public ResponseEntity<TopicResponse> updateTopicStatus(@PathVariable Long id,
                                                           @RequestParam TopicStatus status,
                                                           @AuthenticationPrincipal UserPrincipal userPrincipal) {
        TopicResponse updated = topicService.updateTopicStatus(id, status, userPrincipal.getId());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/topics/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id,
                                            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        topicService.deleteTopic(id, userPrincipal.getId());
        return ResponseEntity.noContent().build();
    }
}
