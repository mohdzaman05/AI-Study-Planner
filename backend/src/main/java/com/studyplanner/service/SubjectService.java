package com.studyplanner.service;

import com.studyplanner.dto.SubjectRequest;
import com.studyplanner.dto.SubjectResponse;
import com.studyplanner.exception.BadRequestException;
import com.studyplanner.exception.ResourceNotFoundException;
import com.studyplanner.model.Subject;
import com.studyplanner.model.TopicStatus;
import com.studyplanner.model.User;
import com.studyplanner.repository.SubjectRepository;
import com.studyplanner.repository.TopicRepository;
import com.studyplanner.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    public SubjectService(SubjectRepository subjectRepository,
                          TopicRepository topicRepository,
                          UserRepository userRepository) {
        this.subjectRepository = subjectRepository;
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<SubjectResponse> getAllSubjects(Long userId) {
        return subjectRepository.findByUserIdOrderByNameAsc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SubjectResponse getSubjectById(Long id, Long userId) {
        Subject subject = findSubjectOrThrow(id, userId);
        return mapToResponse(subject);
    }

    @Transactional
    public SubjectResponse createSubject(SubjectRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (subjectRepository.existsByNameIgnoreCaseAndUserId(request.getName().trim(), userId)) {
            throw new BadRequestException("Subject with name '" + request.getName().trim() + "' already exists");
        }

        Subject subject = new Subject();
        subject.setUser(user);
        subject.setName(request.getName().trim());
        subject.setDescription(request.getDescription());
        subject.setDifficulty(request.getDifficulty());
        subject.setPriority(request.getPriority());
        subject.setExamDate(request.getExamDate());

        Subject saved = subjectRepository.save(subject);
        return mapToResponse(saved);
    }

    @Transactional
    public SubjectResponse updateSubject(Long id, SubjectRequest request, Long userId) {
        Subject subject = findSubjectOrThrow(id, userId);

        subject.setName(request.getName().trim());
        subject.setDescription(request.getDescription());
        subject.setDifficulty(request.getDifficulty());
        subject.setPriority(request.getPriority());
        subject.setExamDate(request.getExamDate());

        Subject updated = subjectRepository.save(subject);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteSubject(Long id, Long userId) {
        Subject subject = findSubjectOrThrow(id, userId);
        subjectRepository.delete(subject);
    }

    public Subject findSubjectOrThrow(Long id, Long userId) {
        return subjectRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));
    }

    public SubjectResponse mapToResponse(Subject subject) {
        long topicCount = topicRepository.countBySubjectId(subject.getId());
        long completedTopicCount = topicRepository.countBySubjectIdAndStatus(subject.getId(), TopicStatus.COMPLETED);
        int progress = topicCount > 0 ? (int) Math.round((double) completedTopicCount / topicCount * 100) : 0;

        return new SubjectResponse(
                subject.getId(),
                subject.getName(),
                subject.getDescription(),
                subject.getDifficulty(),
                subject.getPriority(),
                subject.getExamDate(),
                topicCount,
                completedTopicCount,
                progress,
                subject.getCreatedAt()
        );
    }
}
