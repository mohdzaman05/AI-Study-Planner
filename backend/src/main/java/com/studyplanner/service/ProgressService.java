package com.studyplanner.service;

import com.studyplanner.dto.ProgressOverviewResponse;
import com.studyplanner.dto.SubjectProgressDto;
import com.studyplanner.model.Subject;
import com.studyplanner.model.TaskStatus;
import com.studyplanner.model.TopicStatus;
import com.studyplanner.repository.SubjectRepository;
import com.studyplanner.repository.StudyTaskRepository;
import com.studyplanner.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProgressService {

    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;
    private final StudyTaskRepository studyTaskRepository;

    public ProgressService(SubjectRepository subjectRepository,
                           TopicRepository topicRepository,
                           StudyTaskRepository studyTaskRepository) {
        this.subjectRepository = subjectRepository;
        this.topicRepository = topicRepository;
        this.studyTaskRepository = studyTaskRepository;
    }

    @Transactional(readOnly = true)
    public ProgressOverviewResponse getProgressOverview(Long userId) {
        ProgressOverviewResponse response = new ProgressOverviewResponse();

        List<Subject> subjects = subjectRepository.findByUserIdOrderByNameAsc(userId);
        List<SubjectProgressDto> subjectProgressList = new ArrayList<>();

        long totalTopics = 0;
        long completedTopics = 0;

        for (Subject subject : subjects) {
            long subTotal = topicRepository.countBySubjectId(subject.getId());
            long subCompleted = topicRepository.countBySubjectIdAndStatus(subject.getId(), TopicStatus.COMPLETED);
            int percentage = subTotal > 0 ? (int) Math.round((double) subCompleted / subTotal * 100) : 0;

            totalTopics += subTotal;
            completedTopics += subCompleted;

            subjectProgressList.add(new SubjectProgressDto(
                    subject.getId(),
                    subject.getName(),
                    subject.getDifficulty(),
                    subject.getPriority(),
                    subject.getExamDate(),
                    subTotal,
                    subCompleted,
                    percentage
            ));
        }

        response.setSubjects(subjectProgressList);
        response.setTotalTopics(totalTopics);
        response.setCompletedTopics(completedTopics);
        response.setRemainingTopics(Math.max(0, totalTopics - completedTopics));

        long totalTasks = studyTaskRepository.countByUserId(userId);
        long completedTasks = studyTaskRepository.countByUserIdAndStatus(userId, TaskStatus.COMPLETED);
        long missedTasks = studyTaskRepository.countByUserIdAndStatus(userId, TaskStatus.MISSED);

        response.setTotalTasks(totalTasks);
        response.setCompletedTasks(completedTasks);
        response.setMissedTasks(missedTasks);

        long completedMinutes = studyTaskRepository.sumCompletedMinutesByUserId(userId);
        double studyHours = Math.round((completedMinutes / 60.0) * 10.0) / 10.0;
        response.setTotalStudyHoursLogged(studyHours);

        // Calculate overall progress: weighted blend of topics completed and tasks completed
        int overallPercentage = 0;
        if (totalTopics > 0) {
            overallPercentage = (int) Math.round((double) completedTopics / totalTopics * 100);
        } else if (totalTasks > 0) {
            overallPercentage = (int) Math.round((double) completedTasks / totalTasks * 100);
        }
        response.setOverallProgressPercentage(overallPercentage);

        return response;
    }
}
