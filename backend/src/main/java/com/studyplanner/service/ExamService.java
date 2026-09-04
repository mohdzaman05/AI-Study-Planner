package com.studyplanner.service;

import com.studyplanner.dto.ExamRequest;
import com.studyplanner.dto.ExamResponse;
import com.studyplanner.exception.BadRequestException;
import com.studyplanner.exception.ResourceNotFoundException;
import com.studyplanner.model.Exam;
import com.studyplanner.model.Subject;
import com.studyplanner.model.User;
import com.studyplanner.repository.ExamRepository;
import com.studyplanner.repository.SubjectRepository;
import com.studyplanner.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamService {

    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    public ExamService(ExamRepository examRepository,
                       SubjectRepository subjectRepository,
                       UserRepository userRepository) {
        this.examRepository = examRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ExamResponse> getExams(Long userId) {
        return examRepository.findByUserIdOrderByExamDateAsc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExamResponse getExamById(Long id, Long userId) {
        Exam exam = examRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + id));
        return mapToResponse(exam);
    }

    @Transactional
    public ExamResponse createExam(ExamRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        Subject subject = subjectRepository.findByIdAndUserId(request.getSubjectId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + request.getSubjectId()));

        Exam exam = new Exam();
        exam.setUser(user);
        exam.setSubject(subject);
        exam.setExamDate(request.getExamDate());
        exam.setExamTime(request.getExamTime());
        exam.setImportantTopics(request.getImportantTopics());
        exam.setNotes(request.getNotes());

        Exam saved = examRepository.save(exam);

        // Also update subject examDate if not already set or earlier
        if (subject.getExamDate() == null || request.getExamDate().isBefore(subject.getExamDate())) {
            subject.setExamDate(request.getExamDate());
            subjectRepository.save(subject);
        }

        return mapToResponse(saved);
    }

    @Transactional
    public ExamResponse updateExam(Long id, ExamRequest request, Long userId) {
        Exam exam = examRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + id));

        Subject subject = subjectRepository.findByIdAndUserId(request.getSubjectId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + request.getSubjectId()));

        exam.setSubject(subject);
        exam.setExamDate(request.getExamDate());
        exam.setExamTime(request.getExamTime());
        exam.setImportantTopics(request.getImportantTopics());
        exam.setNotes(request.getNotes());

        Exam updated = examRepository.save(exam);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteExam(Long id, Long userId) {
        Exam exam = examRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + id));
        examRepository.delete(exam);
    }

    public ExamResponse mapToResponse(Exam exam) {
        return new ExamResponse(
                exam.getId(),
                exam.getSubject().getId(),
                exam.getSubject().getName(),
                exam.getExamDate(),
                exam.getExamTime(),
                exam.getImportantTopics(),
                exam.getNotes(),
                exam.getCreatedAt()
        );
    }
}
