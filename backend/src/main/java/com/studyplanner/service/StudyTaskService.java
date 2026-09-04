package com.studyplanner.service;

import com.studyplanner.dto.StudyTaskResponse;
import com.studyplanner.exception.ResourceNotFoundException;
import com.studyplanner.model.StudyTask;
import com.studyplanner.model.TaskStatus;
import com.studyplanner.model.Topic;
import com.studyplanner.model.TopicStatus;
import com.studyplanner.repository.StudyTaskRepository;
import com.studyplanner.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudyTaskService {

    private final StudyTaskRepository studyTaskRepository;
    private final TopicRepository topicRepository;

    public StudyTaskService(StudyTaskRepository studyTaskRepository, TopicRepository topicRepository) {
        this.studyTaskRepository = studyTaskRepository;
        this.topicRepository = topicRepository;
    }

    @Transactional(readOnly = true)
    public List<StudyTaskResponse> getTasks(Long userId, LocalDate date, TaskStatus status) {
        List<StudyTask> tasks;
        if (date != null) {
            tasks = studyTaskRepository.findByUserIdAndTaskDate(userId, date);
        } else {
            tasks = studyTaskRepository.findAllByUserId(userId);
        }

        if (status != null) {
            tasks = tasks.stream()
                    .filter(t -> t.getStatus() == status)
                    .collect(Collectors.toList());
        }

        return tasks.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StudyTaskResponse> getTodayTasks(Long userId) {
        return studyTaskRepository.findByUserIdAndTaskDate(userId, LocalDate.now()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public StudyTaskResponse completeTask(Long taskId, Long userId) {
        return updateTaskStatus(taskId, TaskStatus.COMPLETED, userId);
    }

    @Transactional
    public StudyTaskResponse markTaskMissed(Long taskId, Long userId) {
        return updateTaskStatus(taskId, TaskStatus.MISSED, userId);
    }

    @Transactional
    public StudyTaskResponse updateTaskStatus(Long taskId, TaskStatus status, Long userId) {
        StudyTask task = studyTaskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        task.setStatus(status);
        if (status == TaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());
            // If linked to topic, update topic status to COMPLETED
            if (task.getTopic() != null) {
                Topic topic = task.getTopic();
                topic.setStatus(TopicStatus.COMPLETED);
                topicRepository.save(topic);
            }
        } else {
            task.setCompletedAt(null);
        }

        StudyTask updated = studyTaskRepository.save(task);
        return mapToResponse(updated);
    }

    public StudyTaskResponse mapToResponse(StudyTask task) {
        return new StudyTaskResponse(
                task.getId(),
                task.getStudyPlan().getId(),
                task.getSubject().getId(),
                task.getSubject().getName(),
                task.getTopic() != null ? task.getTopic().getId() : null,
                task.getTopic() != null ? task.getTopic().getName() : "General Revision",
                task.getTaskDate(),
                task.getStartTime(),
                task.getDurationMinutes(),
                task.getPriority(),
                task.getReasonRecommendation(),
                task.getStatus(),
                task.getCompletedAt()
        );
    }
}
