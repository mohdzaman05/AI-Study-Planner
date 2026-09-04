package com.studyplanner.service;

import com.studyplanner.dto.TopicRequest;
import com.studyplanner.dto.TopicResponse;
import com.studyplanner.exception.ResourceNotFoundException;
import com.studyplanner.model.Subject;
import com.studyplanner.model.Topic;
import com.studyplanner.model.TopicStatus;
import com.studyplanner.repository.SubjectRepository;
import com.studyplanner.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final SubjectRepository subjectRepository;

    public TopicService(TopicRepository topicRepository, SubjectRepository subjectRepository) {
        this.topicRepository = topicRepository;
        this.subjectRepository = subjectRepository;
    }

    @Transactional(readOnly = true)
    public List<TopicResponse> getTopicsBySubject(Long subjectId, Long userId) {
        // Validate user ownership of the parent subject
        subjectRepository.findByIdAndUserId(subjectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + subjectId));

        return topicRepository.findBySubjectIdOrderByIdAsc(subjectId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TopicResponse> getAllTopicsForUser(Long userId) {
        return topicRepository.findAllByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TopicResponse createTopic(Long subjectId, TopicRequest request, Long userId) {
        Subject subject = subjectRepository.findByIdAndUserId(subjectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + subjectId));

        Topic topic = new Topic();
        topic.setSubject(subject);
        topic.setName(request.getName().trim());
        topic.setDescription(request.getDescription());
        topic.setDifficulty(request.getDifficulty());
        topic.setEstimatedMinutes(request.getEstimatedMinutes() != null ? request.getEstimatedMinutes() : 45);
        topic.setStatus(request.getStatus() != null ? request.getStatus() : TopicStatus.NOT_STARTED);

        Topic saved = topicRepository.save(topic);
        return mapToResponse(saved);
    }

    @Transactional
    public TopicResponse updateTopic(Long id, TopicRequest request, Long userId) {
        Topic topic = topicRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + id));

        topic.setName(request.getName().trim());
        topic.setDescription(request.getDescription());
        topic.setDifficulty(request.getDifficulty());
        topic.setEstimatedMinutes(request.getEstimatedMinutes());
        if (request.getStatus() != null) {
            topic.setStatus(request.getStatus());
        }

        Topic updated = topicRepository.save(topic);
        return mapToResponse(updated);
    }

    @Transactional
    public TopicResponse updateTopicStatus(Long id, TopicStatus status, Long userId) {
        Topic topic = topicRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + id));

        topic.setStatus(status);
        Topic updated = topicRepository.save(topic);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteTopic(Long id, Long userId) {
        Topic topic = topicRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + id));
        topicRepository.delete(topic);
    }

    public TopicResponse mapToResponse(Topic topic) {
        return new TopicResponse(
                topic.getId(),
                topic.getSubject().getId(),
                topic.getSubject().getName(),
                topic.getName(),
                topic.getDescription(),
                topic.getDifficulty(),
                topic.getEstimatedMinutes(),
                topic.getStatus(),
                topic.getCreatedAt()
        );
    }
}
