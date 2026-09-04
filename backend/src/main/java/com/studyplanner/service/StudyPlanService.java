package com.studyplanner.service;

import com.studyplanner.dto.*;
import com.studyplanner.exception.BadRequestException;
import com.studyplanner.exception.ResourceNotFoundException;
import com.studyplanner.model.*;
import com.studyplanner.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudyPlanService {

    private final StudyPlanRepository studyPlanRepository;
    private final StudyTaskRepository studyTaskRepository;
    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;
    private final ExamRepository examRepository;
    private final UserRepository userRepository;
    private final AiService aiService;
    private final StudyTaskService studyTaskService;

    public StudyPlanService(StudyPlanRepository studyPlanRepository,
                            StudyTaskRepository studyTaskRepository,
                            SubjectRepository subjectRepository,
                            TopicRepository topicRepository,
                            ExamRepository examRepository,
                            UserRepository userRepository,
                            AiService aiService,
                            StudyTaskService studyTaskService) {
        this.studyPlanRepository = studyPlanRepository;
        this.studyTaskRepository = studyTaskRepository;
        this.subjectRepository = subjectRepository;
        this.topicRepository = topicRepository;
        this.examRepository = examRepository;
        this.userRepository = userRepository;
        this.aiService = aiService;
        this.studyTaskService = studyTaskService;
    }

    @Transactional(readOnly = true)
    public Optional<StudyPlanResponse> getActivePlan(Long userId) {
        return studyPlanRepository.findFirstByUserIdAndStatusOrderByGeneratedAtDesc(userId, PlanStatus.ACTIVE)
                .map(this::mapToResponseWithTasks);
    }

    @Transactional(readOnly = true)
    public List<StudyPlanResponse> getAllPlans(Long userId) {
        return studyPlanRepository.findByUserIdOrderByGeneratedAtDesc(userId).stream()
                .map(this::mapToResponseWithTasks)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StudyPlanResponse getPlanById(Long id, Long userId) {
        StudyPlan plan = studyPlanRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Study plan not found with id: " + id));
        return mapToResponseWithTasks(plan);
    }

    @Transactional
    public StudyPlanResponse generatePlan(GeneratePlanRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        List<Subject> subjects = subjectRepository.findByUserIdOrderByNameAsc(userId);
        if (subjects.isEmpty()) {
            throw new BadRequestException("Please add at least one subject before generating a study plan.");
        }

        List<Exam> exams = examRepository.findByUserIdOrderByExamDateAsc(userId);
        List<Topic> topics = topicRepository.findAllByUserId(userId);

        // Archive any previous active plans
        List<StudyPlan> previousActive = studyPlanRepository.findByUserIdOrderByGeneratedAtDesc(userId);
        for (StudyPlan p : previousActive) {
            if (p.getStatus() == PlanStatus.ACTIVE) {
                p.setStatus(PlanStatus.ARCHIVED);
                studyPlanRepository.save(p);
            }
        }

        // Generate study plan tasks
        List<AiService.GeneratedTaskTemplate> taskTemplates = aiService.generateStudyPlan(user, subjects, exams, topics, request);

        LocalDate startDate = LocalDate.now();
        int planDays = request.getPlanDurationDays() != null ? request.getPlanDurationDays() : 14;
        LocalDate endDate = startDate.plusDays(planDays);

        StudyPlan studyPlan = new StudyPlan();
        studyPlan.setUser(user);
        studyPlan.setTitle("AI Study Schedule (" + startDate + " to " + endDate + ")");
        studyPlan.setStartDate(startDate);
        studyPlan.setEndDate(endDate);
        studyPlan.setAvailableHoursPerDay(request.getAvailableHoursPerDay());
        studyPlan.setPreferredStartTime(request.getPreferredStartTime());
        studyPlan.setStatus(PlanStatus.ACTIVE);
        studyPlan.setPromptSummary("Plan covering " + subjects.size() + " subjects with " + request.getAvailableHoursPerDay() + " hrs/day.");

        StudyPlan savedPlan = studyPlanRepository.save(studyPlan);

        // Save generated tasks
        Map<Long, Subject> subjectMap = subjects.stream().collect(Collectors.toMap(Subject::getId, s -> s));
        Map<Long, Topic> topicMap = topics.stream().collect(Collectors.toMap(Topic::getId, t -> t));

        List<StudyTask> tasksToSave = new ArrayList<>();
        for (AiService.GeneratedTaskTemplate template : taskTemplates) {
            Subject subject = subjectMap.get(template.subjectId);
            if (subject == null && !subjects.isEmpty()) {
                subject = subjects.get(0);
            }
            if (subject == null) continue;

            Topic topic = template.topicId != null ? topicMap.get(template.topicId) : null;

            StudyTask task = new StudyTask();
            task.setStudyPlan(savedPlan);
            task.setSubject(subject);
            task.setTopic(topic);
            task.setTaskDate(template.date);
            task.setStartTime(template.startTime);
            task.setDurationMinutes(template.durationMinutes);
            task.setPriority(template.priority != null ? template.priority : Priority.MEDIUM);
            task.setReasonRecommendation(template.reason);
            task.setStatus(TaskStatus.PENDING);

            tasksToSave.add(task);
        }

        List<StudyTask> savedTasks = studyTaskRepository.saveAll(tasksToSave);
        savedPlan.setTasks(savedTasks);

        return mapToResponseWithTasks(savedPlan);
    }

    @Transactional
    public StudyPlanResponse adjustPlan(Long planId, AdjustPlanRequest request, Long userId) {
        StudyPlan plan = studyPlanRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Study plan not found with id: " + planId));

        StudyTask missedTask = null;
        if (request.getMissedTaskId() != null) {
            missedTask = studyTaskRepository.findByIdAndUserId(request.getMissedTaskId(), userId).orElse(null);
            if (missedTask != null) {
                missedTask.setStatus(TaskStatus.MISSED);
                studyTaskRepository.save(missedTask);
            }
        }

        List<StudyTask> allTasks = studyTaskRepository.findByStudyPlanIdOrderByTaskDateAscStartTimeAsc(planId);
        List<StudyTask> pendingTasks = allTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.PENDING)
                .collect(Collectors.toList());

        List<Subject> subjects = subjectRepository.findByUserIdOrderByNameAsc(userId);
        List<Exam> exams = examRepository.findByUserIdOrderByExamDateAsc(userId);

        List<AiService.GeneratedTaskTemplate> adjustedTemplates = aiService.adjustStudyPlan(
                plan, pendingTasks, missedTask, subjects, exams, request.getReason());

        // Remove old pending tasks and replace with redistributed tasks
        studyTaskRepository.deleteAll(pendingTasks);

        Map<Long, Subject> subjectMap = subjects.stream().collect(Collectors.toMap(Subject::getId, s -> s));
        List<Topic> topics = topicRepository.findAllByUserId(userId);
        Map<Long, Topic> topicMap = topics.stream().collect(Collectors.toMap(Topic::getId, t -> t));

        List<StudyTask> newTasks = new ArrayList<>();
        for (AiService.GeneratedTaskTemplate template : adjustedTemplates) {
            Subject subject = subjectMap.get(template.subjectId);
            if (subject == null && !subjects.isEmpty()) subject = subjects.get(0);
            if (subject == null) continue;

            Topic topic = template.topicId != null ? topicMap.get(template.topicId) : null;

            StudyTask task = new StudyTask();
            task.setStudyPlan(plan);
            task.setSubject(subject);
            task.setTopic(topic);
            task.setTaskDate(template.date);
            task.setStartTime(template.startTime);
            task.setDurationMinutes(template.durationMinutes);
            task.setPriority(template.priority != null ? template.priority : Priority.MEDIUM);
            task.setReasonRecommendation(template.reason);
            task.setStatus(TaskStatus.PENDING);

            newTasks.add(task);
        }

        studyTaskRepository.saveAll(newTasks);

        return getPlanById(planId, userId);
    }

    private StudyPlanResponse mapToResponseWithTasks(StudyPlan plan) {
        List<StudyTask> tasks = studyTaskRepository.findByStudyPlanIdOrderByTaskDateAscStartTimeAsc(plan.getId());
        List<StudyTaskResponse> taskResponses = tasks.stream()
                .map(studyTaskService::mapToResponse)
                .collect(Collectors.toList());

        return new StudyPlanResponse(
                plan.getId(),
                plan.getTitle(),
                plan.getGeneratedAt(),
                plan.getStartDate(),
                plan.getEndDate(),
                plan.getAvailableHoursPerDay(),
                plan.getPreferredStartTime(),
                plan.getStatus(),
                plan.getPromptSummary(),
                taskResponses
        );
    }
}
