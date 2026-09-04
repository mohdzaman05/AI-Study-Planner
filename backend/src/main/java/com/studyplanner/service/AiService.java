package com.studyplanner.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyplanner.dto.GeneratePlanRequest;
import com.studyplanner.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class AiService {

    private static final Logger logger = LoggerFactory.getLogger(AiService.class);

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Value("${gemini.api.model:gemini-1.5-flash}")
    private String geminiModel;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models}")
    private String geminiApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public static class GeneratedTaskTemplate {
        public Long subjectId;
        public Long topicId;
        public String topicName;
        public LocalDate date;
        public String startTime;
        public int durationMinutes;
        public Priority priority;
        public String reason;

        public GeneratedTaskTemplate(Long subjectId, Long topicId, String topicName, LocalDate date,
                                     String startTime, int durationMinutes, Priority priority, String reason) {
            this.subjectId = subjectId;
            this.topicId = topicId;
            this.topicName = topicName;
            this.date = date;
            this.startTime = startTime;
            this.durationMinutes = durationMinutes;
            this.priority = priority;
            this.reason = reason;
        }
    }

    /**
     * Generates a study plan using Gemini API if key is available, or fallback heuristic engine.
     */
    public List<GeneratedTaskTemplate> generateStudyPlan(User user,
                                                         List<Subject> subjects,
                                                         List<Exam> exams,
                                                         List<Topic> topics,
                                                         GeneratePlanRequest request) {
        if (StringUtils.hasText(geminiApiKey) && !geminiApiKey.startsWith("your_")) {
            try {
                List<GeneratedTaskTemplate> planFromAi = callGeminiForPlan(subjects, exams, topics, request);
                if (planFromAi != null && !planFromAi.isEmpty()) {
                    logger.info("Successfully generated study plan using Gemini API");
                    return planFromAi;
                }
            } catch (Exception e) {
                logger.warn("Failed to generate plan using Gemini API, falling back to heuristic engine: {}", e.getMessage());
            }
        }

        logger.info("Generating study plan using Intelligent Heuristic Engine");
        return generateHeuristicPlan(subjects, exams, topics, request);
    }

    /**
     * Smart Plan Adjustment: Rebalances tasks when tasks are missed.
     */
    public List<GeneratedTaskTemplate> adjustStudyPlan(StudyPlan currentPlan,
                                                       List<StudyTask> pendingTasks,
                                                       StudyTask missedTask,
                                                       List<Subject> subjects,
                                                       List<Exam> exams,
                                                       String reason) {
        logger.info("Adjusting study plan for missed task id: {}", missedTask != null ? missedTask.getId() : "none");

        // Determine remaining days from today
        LocalDate today = LocalDate.now();
        LocalDate endDate = currentPlan.getEndDate().isAfter(today) ? currentPlan.getEndDate() : today.plusDays(7);
        long daysRemaining = ChronoUnit.DAYS.between(today, endDate);
        if (daysRemaining <= 0) {
            daysRemaining = 7;
            endDate = today.plusDays(7);
        }

        List<GeneratedTaskTemplate> rescheduled = new ArrayList<>();
        double dailyHours = currentPlan.getAvailableHoursPerDay() != null ? currentPlan.getAvailableHoursPerDay() : 3.0;
        int maxDailyMinutes = (int) (dailyHours * 60);

        // Group existing pending tasks by date
        Map<LocalDate, List<StudyTask>> tasksByDate = new HashMap<>();
        for (StudyTask task : pendingTasks) {
            if (!task.getTaskDate().isBefore(today)) {
                tasksByDate.computeIfAbsent(task.getTaskDate(), d -> new ArrayList<>()).add(task);
            }
        }

        // Intelligently slot the missed task on a future date that has capacity
        // (Avoiding overloading tomorrow, which violates realistic planning principle)
        boolean missedTaskPlaced = false;
        LocalDate candidateDate = today.plusDays(1);

        for (int i = 1; i <= daysRemaining; i++) {
            LocalDate date = today.plusDays(i);
            List<StudyTask> dayTasks = tasksByDate.getOrDefault(date, Collections.emptyList());
            int totalMin = dayTasks.stream().mapToInt(StudyTask::getDurationMinutes).sum();

            if (totalMin + 45 <= maxDailyMinutes) {
                // Found a date with room!
                candidateDate = date;
                missedTaskPlaced = true;
                break;
            }
        }

        // Reconstruct remaining schedule with the missed task distributed
        LocalTime startTime = parseStartTime(currentPlan.getPreferredStartTime());
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (int i = 0; i <= daysRemaining; i++) {
            LocalDate date = today.plusDays(i);
            List<StudyTask> dayTasks = tasksByDate.getOrDefault(date, new ArrayList<>());
            LocalTime currentSlotTime = startTime;

            for (StudyTask task : dayTasks) {
                rescheduled.add(new GeneratedTaskTemplate(
                        task.getSubject().getId(),
                        task.getTopic() != null ? task.getTopic().getId() : null,
                        task.getTopic() != null ? task.getTopic().getName() : "Revision",
                        date,
                        currentSlotTime.format(timeFormatter),
                        task.getDurationMinutes(),
                        task.getPriority(),
                        task.getReasonRecommendation()
                ));
                currentSlotTime = currentSlotTime.plusMinutes(task.getDurationMinutes() + 15); // with 15 min rest
            }

            // If this is the chosen candidate date, insert the rescheduled missed task
            if (missedTask != null && date.equals(candidateDate) && missedTaskPlaced) {
                rescheduled.add(new GeneratedTaskTemplate(
                        missedTask.getSubject().getId(),
                        missedTask.getTopic() != null ? missedTask.getTopic().getId() : null,
                        missedTask.getTopic() != null ? missedTask.getTopic().getName() : "Missed Topic Catch-up",
                        date,
                        currentSlotTime.format(timeFormatter),
                        45,
                        Priority.HIGH,
                        "Smart Plan Adjustment: Redistributed from earlier missed session without overloading your schedule."
                ));
            }
        }

        return rescheduled;
    }

    /**
     * AI Study Assistant chat implementation.
     */
    public String chatWithAi(String userMessage, Long userId, List<Subject> subjects, List<Exam> exams) {
        if (StringUtils.hasText(geminiApiKey) && !geminiApiKey.startsWith("your_")) {
            try {
                String aiReply = callGeminiForChat(userMessage, subjects, exams);
                if (StringUtils.hasText(aiReply)) {
                    return aiReply;
                }
            } catch (Exception e) {
                logger.warn("Gemini chat API failed, using intelligent assistant fallback: {}", e.getMessage());
            }
        }

        return generateAssistantResponse(userMessage, subjects, exams);
    }

    // =========================================================================
    // Heuristic Engine: Deterministic, realistic study schedule generator
    // =========================================================================
    private List<GeneratedTaskTemplate> generateHeuristicPlan(List<Subject> subjects,
                                                             List<Exam> exams,
                                                             List<Topic> topics,
                                                             GeneratePlanRequest request) {
        List<GeneratedTaskTemplate> plan = new ArrayList<>();
        if (subjects.isEmpty()) {
            return plan;
        }

        LocalDate startDate = LocalDate.now();
        int durationDays = request.getPlanDurationDays() != null ? request.getPlanDurationDays() : 14;
        double dailyHours = request.getAvailableHoursPerDay() != null ? request.getAvailableHoursPerDay() : 3.0;
        int maxDailyMinutes = (int) (dailyHours * 60);

        LocalTime baseStartTime = parseStartTime(request.getPreferredStartTime());
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // Map exam dates to subjects
        Map<Long, LocalDate> subjectExamMap = new HashMap<>();
        for (Exam exam : exams) {
            subjectExamMap.put(exam.getSubject().getId(), exam.getExamDate());
        }

        // Filter and sort topics by urgency & difficulty
        List<Topic> uncompletedTopics = new ArrayList<>();
        for (Topic t : topics) {
            if (t.getStatus() != TopicStatus.COMPLETED) {
                uncompletedTopics.add(t);
            }
        }

        // Sort: High priority subject first, closest exam first, hard difficulty first
        uncompletedTopics.sort((t1, t2) -> {
            LocalDate e1 = subjectExamMap.getOrDefault(t1.getSubject().getId(), LocalDate.MAX);
            LocalDate e2 = subjectExamMap.getOrDefault(t2.getSubject().getId(), LocalDate.MAX);
            int examCompare = e1.compareTo(e2);
            if (examCompare != 0) return examCompare;

            int p1 = getPriorityScore(t1.getSubject().getPriority());
            int p2 = getPriorityScore(t2.getSubject().getPriority());
            if (p1 != p2) return Integer.compare(p2, p1);

            int d1 = getDifficultyScore(t1.getDifficulty());
            int d2 = getDifficultyScore(t2.getDifficulty());
            return Integer.compare(d2, d1);
        });

        int topicIndex = 0;
        int totalTopics = uncompletedTopics.size();

        for (int day = 0; day < durationDays; day++) {
            LocalDate currentDate = startDate.plusDays(day);
            int minutesScheduledToday = 0;
            LocalTime currentSlotTime = baseStartTime;

            // Schedule tasks for the day within maxDailyMinutes
            while (minutesScheduledToday + 40 <= maxDailyMinutes) {
                if (topicIndex < totalTopics) {
                    Topic topic = uncompletedTopics.get(topicIndex);
                    int taskDuration = Math.min(topic.getEstimatedMinutes() != null ? topic.getEstimatedMinutes() : 45,
                            maxDailyMinutes - minutesScheduledToday);

                    // Cap single task duration to 60 minutes for student focus
                    taskDuration = Math.min(taskDuration, 60);
                    if (taskDuration < 30) taskDuration = 30;

                    String reason = generateReason(topic, subjectExamMap.get(topic.getSubject().getId()), currentDate);

                    plan.add(new GeneratedTaskTemplate(
                            topic.getSubject().getId(),
                            topic.getId(),
                            topic.getName(),
                            currentDate,
                            currentSlotTime.format(timeFormatter),
                            taskDuration,
                            topic.getSubject().getPriority(),
                            reason
                    ));

                    minutesScheduledToday += taskDuration;
                    currentSlotTime = currentSlotTime.plusMinutes(taskDuration + 15); // 15 min rest break
                    topicIndex++;
                } else {
                    // All uncompleted topics covered! Schedule active recall / revision for upcoming exams
                    Subject targetSubject = subjects.get(day % subjects.size());
                    int revisionDuration = Math.min(45, maxDailyMinutes - minutesScheduledToday);
                    if (revisionDuration < 30) break;

                    plan.add(new GeneratedTaskTemplate(
                            targetSubject.getId(),
                            null,
                            "Practice & Active Recall: " + targetSubject.getName(),
                            currentDate,
                            currentSlotTime.format(timeFormatter),
                            revisionDuration,
                            targetSubject.getPriority(),
                            "Spaced repetition and practice problems to solidify long-term retention before exam day."
                    ));

                    minutesScheduledToday += revisionDuration;
                    currentSlotTime = currentSlotTime.plusMinutes(revisionDuration + 15);
                    break;
                }
            }
        }

        return plan;
    }

    private String generateReason(Topic topic, LocalDate examDate, LocalDate taskDate) {
        StringBuilder sb = new StringBuilder();
        if (examDate != null) {
            long daysToExam = ChronoUnit.DAYS.between(taskDate, examDate);
            if (daysToExam <= 7) {
                sb.append("URGENT: Exam in ").append(daysToExam).append(" days! ");
            } else if (daysToExam <= 14) {
                sb.append("Upcoming exam in ").append(daysToExam).append(" days. ");
            }
        }

        if (topic.getDifficulty() == Difficulty.HARD) {
            sb.append("High difficulty concept; scheduled during peak energy window.");
        } else if (topic.getSubject().getPriority() == Priority.HIGH) {
            sb.append("Core high-priority subject topic. Essential foundation.");
        } else {
            sb.append("Standard curriculum milestone to maintain steady study velocity.");
        }

        return sb.toString();
    }

    private int getPriorityScore(Priority p) {
        if (p == null) return 2;
        return switch (p) {
            case HIGH -> 3;
            case MEDIUM -> 2;
            case LOW -> 1;
        };
    }

    private int getDifficultyScore(Difficulty d) {
        if (d == null) return 2;
        return switch (d) {
            case HARD -> 3;
            case MEDIUM -> 2;
            case EASY -> 1;
        };
    }

    private LocalTime parseStartTime(String timeStr) {
        try {
            if (StringUtils.hasText(timeStr)) {
                return LocalTime.parse(timeStr.trim());
            }
        } catch (Exception ignored) {
        }
        return LocalTime.of(18, 0);
    }

    // =========================================================================
    // Gemini API Direct Integration
    // =========================================================================
    private List<GeneratedTaskTemplate> callGeminiForPlan(List<Subject> subjects,
                                                          List<Exam> exams,
                                                          List<Topic> topics,
                                                          GeneratePlanRequest request) {
        String prompt = buildPlanPrompt(subjects, exams, topics, request);
        String url = String.format("%s/%s:generateContent?key=%s", geminiApiUrl, geminiModel, geminiApiKey);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                ),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json",
                        "temperature", 0.3
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return parseGeminiPlanResponse(response.getBody(), subjects, topics);
        }

        return Collections.emptyList();
    }

    private String buildPlanPrompt(List<Subject> subjects, List<Exam> exams, List<Topic> topics, GeneratePlanRequest req) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an expert academic study planner. Generate a realistic JSON study schedule for a college student.\n");
        sb.append("Available daily hours: ").append(req.getAvailableHoursPerDay()).append(" hours.\n");
        sb.append("Preferred start time: ").append(req.getPreferredStartTime()).append(".\n");
        sb.append("Current Date: ").append(LocalDate.now()).append(".\n");
        sb.append("Subjects:\n");
        for (Subject s : subjects) {
            sb.append(String.format("- Subject ID %d: %s (Difficulty: %s, Priority: %s, Exam: %s)\n",
                    s.getId(), s.getName(), s.getDifficulty(), s.getPriority(), s.getExamDate()));
        }
        sb.append("Topics:\n");
        for (Topic t : topics) {
            sb.append(String.format("- Topic ID %d under Subject ID %d: %s (Difficulty: %s, Est: %d mins, Status: %s)\n",
                    t.getId(), t.getSubject().getId(), t.getName(), t.getDifficulty(), t.getEstimatedMinutes(), t.getStatus()));
        }
        sb.append("Upcoming Exams:\n");
        for (Exam e : exams) {
            sb.append(String.format("- Exam: %s on %s (Important: %s)\n",
                    e.getSubject().getName(), e.getExamDate(), e.getImportantTopics()));
        }

        sb.append("\nReturn ONLY a JSON array with objects in this schema:\n");
        sb.append("[{\n");
        sb.append("  \"subjectId\": 1,\n");
        sb.append("  \"topicId\": 1,\n");
        sb.append("  \"topicName\": \"Topic Title\",\n");
        sb.append("  \"date\": \"YYYY-MM-DD\",\n");
        sb.append("  \"startTime\": \"HH:mm\",\n");
        sb.append("  \"durationMinutes\": 45,\n");
        sb.append("  \"priority\": \"HIGH\",\n");
        sb.append("  \"reason\": \"Reason from AI\"\n");
        sb.append("}]\n");
        return sb.toString();
    }

    private List<GeneratedTaskTemplate> parseGeminiPlanResponse(String jsonString, List<Subject> subjects, List<Topic> topics) {
        List<GeneratedTaskTemplate> list = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(jsonString);
            JsonNode textNode = root.path("candidates").get(0).path("content").path("parts").get(0).path("text");
            String rawText = textNode.asText();

            JsonNode tasksArray = objectMapper.readTree(rawText);
            if (tasksArray.isArray()) {
                for (JsonNode item : tasksArray) {
                    Long subjectId = item.path("subjectId").asLong();
                    Long topicId = item.has("topicId") && !item.path("topicId").isNull() ? item.path("topicId").asLong() : null;
                    String topicName = item.path("topicName").asText("Study Session");
                    LocalDate date = LocalDate.parse(item.path("date").asText());
                    String startTime = item.path("startTime").asText("18:00");
                    int duration = item.path("durationMinutes").asInt(45);
                    Priority priority = Priority.valueOf(item.path("priority").asText("MEDIUM").toUpperCase());
                    String reason = item.path("reason").asText("AI Scheduled");

                    list.add(new GeneratedTaskTemplate(subjectId, topicId, topicName, date, startTime, duration, priority, reason));
                }
            }
        } catch (Exception e) {
            logger.warn("Could not parse Gemini JSON response, falling back: {}", e.getMessage());
        }
        return list;
    }

    private String callGeminiForChat(String userMessage, List<Subject> subjects, List<Exam> exams) {
        String url = String.format("%s/%s:generateContent?key=%s", geminiApiUrl, geminiModel, geminiApiKey);

        StringBuilder context = new StringBuilder();
        context.append("You are an intelligent, supportive AI Study Assistant for a university student. ");
        context.append("The student is enrolled in the following subjects: ");
        for (Subject s : subjects) {
            context.append(s.getName()).append(" (Priority: ").append(s.getPriority()).append("); ");
        }
        context.append(". Current date is ").append(LocalDate.now()).append(". ");
        context.append("Provide clear, pedagogical, concise, student-friendly responses with practical examples and study advice.");

        Map<String, Object> requestBody = Map.of(
                "systemInstruction", Map.of(
                        "parts", List.of(Map.of("text", context.toString()))
                ),
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", userMessage)
                        ))
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            try {
                JsonNode root = objectMapper.readTree(response.getBody());
                return root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private String generateAssistantResponse(String message, List<Subject> subjects, List<Exam> exams) {
        String lower = message.toLowerCase();

        if (lower.contains("linked list") || lower.contains("linkedlist")) {
            return "### Understanding Linked Lists Simply\n\n" +
                    "Think of a **Linked List** like a **scavenger hunt** or a **train made of cars connected together**:\n\n" +
                    "1. **Nodes**: Each car contains two things:\n" +
                    "   - The **Data** (e.g., a student's roll number).\n" +
                    "   - A **Pointer (Next)** pointing to where the next car is located in memory.\n" +
                    "2. **Head**: The engine of the train (entry point). If you lose the head, you lose the whole list!\n" +
                    "3. **Tail**: The last car, which points to `null`.\n\n" +
                    "**Why use it over an Array?**\n" +
                    "- In arrays, inserting at the beginning requires shifting every single element (`O(n)`).\n" +
                    "- In a linked list, you just update one pointer (`O(1)`), without touching any other node!\n\n" +
                    "💡 *Pro-Tip for Exams*: Always practice handling **edge cases** like an empty list (`head == null`) and single-element lists.";
        }

        if (lower.contains("prioritize") || lower.contains("3 days") || lower.contains("few days") || lower.contains("exam prep")) {
            StringBuilder sb = new StringBuilder();
            sb.append("### High-Impact 3-Day Exam Strategy 🎯\n\n");
            sb.append("When you have only a few days left, apply the **80/20 Pareto Rule**:\n\n");
            sb.append("1. **Day 1: High-Weightage Core Concepts (Active Recall)**\n");
            sb.append("   - Review previous years' exam papers and question patterns.\n");
            sb.append("   - Focus strictly on High-Priority and Hard topics first.\n\n");
            sb.append("2. **Day 2: Problem Solving & Diagrams**\n");
            sb.append("   - Solve 5-8 representative problems by hand without looking at solutions.\n");
            sb.append("   - Memorize key schemas, algorithms, or formulas.\n\n");
            sb.append("3. **Day 3: Rapid Fire Revision & Mock Test**\n");
            sb.append("   - Explain concepts aloud using the **Feynman Technique**.\n");
            sb.append("   - Sleep at least 7 hours—sleep is when your brain consolidates neural connections!\n\n");

            if (!exams.isEmpty()) {
                Exam next = exams.get(0);
                sb.append("📌 *Your nearest exam is **").append(next.getSubject().getName())
                        .append("** on ").append(next.getExamDate()).append(".* Focus on: ")
                        .append(StringUtils.hasText(next.getImportantTopics()) ? next.getImportantTopics() : "core syllabus topics")
                        .append(".");
            }
            return sb.toString();
        }

        if (lower.contains("revision") || lower.contains("tips") || lower.contains("study tips")) {
            return "### Top Evidence-Based Revision Techniques 📚\n\n" +
                    "1. **Active Recall**: Test yourself with closed notes. Generating answers from memory builds stronger synaptic pathways than passive rereading.\n" +
                    "2. **Pomodoro Technique**: 25 minutes of intense focus + 5-minute break. After 4 cycles, take a longer 20-minute break.\n" +
                    "3. **Spaced Repetition**: Review topics at increasing intervals (Day 1, Day 3, Day 7) to beat the Ebbinghaus forgetting curve.\n" +
                    "4. **Feynman Technique**: Try explaining the topic in simple terms to a 10-year-old. Wherever you get stuck, that's your knowledge gap to revisit.";
        }

        if (lower.contains("java") || lower.contains("oop") || lower.contains("object oriented")) {
            return "### Core Java OOP Principles ☕\n\n" +
                    "1. **Encapsulation**: Bundling data (variables) and methods inside a class, keeping fields `private` and exposing `getters`/`setters`.\n" +
                    "2. **Inheritance**: Code reusability where a child class inherits properties from a parent using `extends`.\n" +
                    "3. **Polymorphism**: 'Many forms'. *Method Overloading* (compile-time) and *Method Overriding* (runtime with `@Override`).\n" +
                    "4. **Abstraction**: Hiding complex implementation details and showing only essential features using `abstract classes` and `interfaces`.\n\n" +
                    "💡 *Exam Tip*: Be prepared to draw a UML diagram and write a 15-line code snippet showing an Interface and its implementing class.";
        }

        // Generic helpful academic response
        return "### Study Assistant Recommendation 💡\n\n" +
                "I'm here to help you excel in your academic journey! Here is what I can do for you:\n\n" +
                "- **Concept Explanations**: Ask me to break down tricky topics (e.g., *'Explain Binary Search Trees'* or *'What is Normalization in DBMS?'*).\n" +
                "- **Exam Prioritization**: Tell me how many days you have left and I'll outline high-yield topics.\n" +
                "- **Study Methods**: Ask for revision strategies like Active Recall or Pomodoro.\n\n" +
                "What specific topic or subject would you like to conquer today?";
    }
}
