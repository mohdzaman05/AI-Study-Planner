package com.studyplanner.config;

import com.studyplanner.model.*;
import com.studyplanner.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;
    private final ExamRepository examRepository;
    private final StudyPlanRepository studyPlanRepository;
    private final StudyTaskRepository studyTaskRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           SubjectRepository subjectRepository,
                           TopicRepository topicRepository,
                           ExamRepository examRepository,
                           StudyPlanRepository studyPlanRepository,
                           StudyTaskRepository studyTaskRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.topicRepository = topicRepository;
        this.examRepository = examRepository;
        this.studyPlanRepository = studyPlanRepository;
        this.studyTaskRepository = studyTaskRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("demo@example.com")) {
            logger.info("Seeding initial demo student account and curriculum data...");

            User demoUser = new User();
            demoUser.setFullName("Demo Student");
            demoUser.setEmail("demo@example.com");
            demoUser.setPasswordHash(passwordEncoder.encode("Demo@12345"));
            demoUser.setDailyStudyHours(3.5);
            demoUser.setPreferredStudyTime("17:00");
            demoUser = userRepository.save(demoUser);

            // Subject 1: DSA
            Subject dsa = new Subject();
            dsa.setUser(demoUser);
            dsa.setName("Data Structures & Algorithms");
            dsa.setDescription("Core computer science curriculum covering linear and non-linear data structures, algorithm analysis, and problem-solving.");
            dsa.setDifficulty(Difficulty.HARD);
            dsa.setPriority(Priority.HIGH);
            dsa.setExamDate(LocalDate.now().plusDays(10));
            dsa = subjectRepository.save(dsa);

            createTopic(dsa, "Arrays & Two-Pointer Techniques", "Static and dynamic arrays, prefix sums, and sliding window paradigms.", Difficulty.MEDIUM, 45, TopicStatus.COMPLETED);
            createTopic(dsa, "Singly & Doubly Linked Lists", "Node manipulation, cycle detection using Floyd's algorithm, and list reversals.", Difficulty.MEDIUM, 50, TopicStatus.COMPLETED);
            createTopic(dsa, "Stacks & Queues Applications", "LIFO/FIFO mechanisms, monotonic stacks, and breadth-first search queues.", Difficulty.EASY, 40, TopicStatus.COMPLETED);
            createTopic(dsa, "Binary Trees & BST Search", "Tree traversals (Inorder, Preorder, Postorder) and Binary Search Tree validation.", Difficulty.HARD, 60, TopicStatus.IN_PROGRESS);
            createTopic(dsa, "Graph Traversals (BFS & DFS)", "Adjacency lists, cycle detection in directed graphs, and topological sorting.", Difficulty.HARD, 60, TopicStatus.NOT_STARTED);
            createTopic(dsa, "Dynamic Programming Basics", "Overlapping subproblems, optimal substructure, memoization vs tabulation.", Difficulty.HARD, 60, TopicStatus.NOT_STARTED);

            // Subject 2: DBMS
            Subject dbms = new Subject();
            dbms.setUser(demoUser);
            dbms.setName("Database Management Systems");
            dbms.setDescription("Relational database design, relational algebra, SQL optimization, and ACID concurrency control.");
            dbms.setDifficulty(Difficulty.MEDIUM);
            dbms.setPriority(Priority.HIGH);
            dbms.setExamDate(LocalDate.now().plusDays(18));
            dbms = subjectRepository.save(dbms);

            createTopic(dbms, "Entity-Relationship (ER) Modeling", "Entities, attributes, relationships, cardinalities, and ER-to-Relational conversion.", Difficulty.EASY, 40, TopicStatus.COMPLETED);
            createTopic(dbms, "Relational Schema Normalization", "1NF, 2NF, 3NF, BCNF decomposition and functional dependencies.", Difficulty.HARD, 55, TopicStatus.IN_PROGRESS);
            createTopic(dbms, "Complex SQL Joins & Subqueries", "Inner/outer joins, aggregation, grouping, and correlated subqueries.", Difficulty.MEDIUM, 50, TopicStatus.COMPLETED);
            createTopic(dbms, "Transactions & ACID Properties", "Concurrency anomalies, serializability, two-phase locking (2PL).", Difficulty.MEDIUM, 45, TopicStatus.NOT_STARTED);
            createTopic(dbms, "Indexing & B+ Trees", "Dense vs sparse indexing, clustered indexes, and B-tree search complexity.", Difficulty.HARD, 50, TopicStatus.NOT_STARTED);

            // Subject 3: Operating Systems
            Subject os = new Subject();
            os.setUser(demoUser);
            os.setName("Operating Systems");
            os.setDescription("Process scheduling, multithreading, synchronization primitives, memory paging, and storage subsystems.");
            os.setDifficulty(Difficulty.MEDIUM);
            os.setPriority(Priority.MEDIUM);
            os.setExamDate(LocalDate.now().plusDays(25));
            os = subjectRepository.save(os);

            createTopic(os, "Process Management & Scheduling", "Process state transitions, PCB, Round Robin, SJF, and Priority scheduling.", Difficulty.MEDIUM, 45, TopicStatus.COMPLETED);
            createTopic(os, "Threads & Synchronization Primitives", "Critical section problem, mutexes, semaphores, and race conditions.", Difficulty.HARD, 50, TopicStatus.NOT_STARTED);
            createTopic(os, "Deadlocks & Banker's Algorithm", "Four conditions for deadlock, prevention, avoidance, and safety algorithm.", Difficulty.MEDIUM, 45, TopicStatus.NOT_STARTED);
            createTopic(os, "Virtual Memory & Paging", "Page tables, TLB cache, page replacement algorithms (FIFO, LRU, Optimal).", Difficulty.HARD, 55, TopicStatus.NOT_STARTED);

            // Exams
            Exam exam1 = new Exam();
            exam1.setUser(demoUser);
            exam1.setSubject(dsa);
            exam1.setExamDate(LocalDate.now().plusDays(10));
            exam1.setExamTime("09:30");
            exam1.setImportantTopics("Binary Trees, BST search, Graphs BFS/DFS, Recursion");
            exam1.setNotes("Room 304, Engineering Hall. Bring scientific calculator and student ID.");
            examRepository.save(exam1);

            Exam exam2 = new Exam();
            exam2.setUser(demoUser);
            exam2.setSubject(dbms);
            exam2.setExamDate(LocalDate.now().plusDays(18));
            exam2.setExamTime("14:00");
            exam2.setImportantTopics("Normalization (3NF vs BCNF), ACID, 2PL, B+ Trees");
            exam2.setNotes("Comprehensive semester exam. Focus on functional dependency questions.");
            examRepository.save(exam2);

            // Initial Active Study Plan
            StudyPlan plan = new StudyPlan();
            plan.setUser(demoUser);
            plan.setTitle("Comprehensive Semester Prep Schedule");
            plan.setStartDate(LocalDate.now());
            plan.setEndDate(LocalDate.now().plusDays(14));
            plan.setAvailableHoursPerDay(3.5);
            plan.setPreferredStartTime("17:00");
            plan.setStatus(PlanStatus.ACTIVE);
            plan.setPromptSummary("Pre-configured study plan for DSA and DBMS semester finals.");
            plan = studyPlanRepository.save(plan);

            // Create Today's sample tasks
            LocalDate today = LocalDate.now();
            List<Topic> dsaTopics = topicRepository.findBySubjectIdOrderByIdAsc(dsa.getId());
            List<Topic> dbmsTopics = topicRepository.findBySubjectIdOrderByIdAsc(dbms.getId());

            List<StudyTask> sampleTasks = new ArrayList<>();

            // Task 1: Completed today
            StudyTask t1 = new StudyTask();
            t1.setStudyPlan(plan);
            t1.setSubject(dsa);
            t1.setTopic(dsaTopics.size() > 0 ? dsaTopics.get(0) : null);
            t1.setTaskDate(today);
            t1.setStartTime("17:00");
            t1.setDurationMinutes(45);
            t1.setPriority(Priority.HIGH);
            t1.setReasonRecommendation("Foundation review: High exam frequency question patterns.");
            t1.setStatus(TaskStatus.COMPLETED);
            t1.setCompletedAt(LocalDateTime.now().minusHours(2));
            sampleTasks.add(t1);

            // Task 2: Completed today
            StudyTask t2 = new StudyTask();
            t2.setStudyPlan(plan);
            t2.setSubject(dbms);
            t2.setTopic(dbmsTopics.size() > 0 ? dbmsTopics.get(0) : null);
            t2.setTaskDate(today);
            t2.setStartTime("18:00");
            t2.setDurationMinutes(40);
            t2.setPriority(Priority.HIGH);
            t2.setReasonRecommendation("Prerequisite concept for upcoming normalization modules.");
            t2.setStatus(TaskStatus.COMPLETED);
            t2.setCompletedAt(LocalDateTime.now().minusHours(1));
            sampleTasks.add(t2);

            // Task 3: Completed today
            StudyTask t3 = new StudyTask();
            t3.setStudyPlan(plan);
            t3.setSubject(dsa);
            t3.setTopic(dsaTopics.size() > 1 ? dsaTopics.get(1) : null);
            t3.setTaskDate(today);
            t3.setStartTime("18:55");
            t3.setDurationMinutes(50);
            t3.setPriority(Priority.HIGH);
            t3.setReasonRecommendation("Core data structure: Fast pointer and cycle detection practice.");
            t3.setStatus(TaskStatus.COMPLETED);
            t3.setCompletedAt(LocalDateTime.now().minusMinutes(30));
            sampleTasks.add(t3);

            // Task 4: Pending today
            StudyTask t4 = new StudyTask();
            t4.setStudyPlan(plan);
            t4.setSubject(dsa);
            t4.setTopic(dsaTopics.size() > 3 ? dsaTopics.get(3) : null);
            t4.setTaskDate(today);
            t4.setStartTime("20:00");
            t4.setDurationMinutes(60);
            t4.setPriority(Priority.HIGH);
            t4.setReasonRecommendation("High urgency: DSA Exam is in 10 days! Focus on BST validation algorithms.");
            t4.setStatus(TaskStatus.PENDING);
            sampleTasks.add(t4);

            // Task 5: Pending today
            StudyTask t5 = new StudyTask();
            t5.setStudyPlan(plan);
            t5.setSubject(dbms);
            t5.setTopic(dbmsTopics.size() > 1 ? dbmsTopics.get(1) : null);
            t5.setTaskDate(today);
            t5.setStartTime("21:15");
            t5.setDurationMinutes(55);
            t5.setPriority(Priority.HIGH);
            t5.setReasonRecommendation("Crucial exam topic: Practice 3NF vs BCNF lossy vs lossless decomposition.");
            t5.setStatus(TaskStatus.PENDING);
            sampleTasks.add(t5);

            // Tomorrow tasks
            LocalDate tomorrow = today.plusDays(1);
            StudyTask t6 = new StudyTask();
            t6.setStudyPlan(plan);
            t6.setSubject(dsa);
            t6.setTopic(dsaTopics.size() > 4 ? dsaTopics.get(4) : null);
            t6.setTaskDate(tomorrow);
            t6.setStartTime("17:00");
            t6.setDurationMinutes(60);
            t6.setPriority(Priority.HIGH);
            t6.setReasonRecommendation("Master BFS/DFS cycle detection algorithms.");
            t6.setStatus(TaskStatus.PENDING);
            sampleTasks.add(t6);

            StudyTask t7 = new StudyTask();
            t7.setStudyPlan(plan);
            t7.setSubject(dbms);
            t7.setTopic(dbmsTopics.size() > 2 ? dbmsTopics.get(2) : null);
            t7.setTaskDate(tomorrow);
            t7.setStartTime("18:15");
            t7.setDurationMinutes(50);
            t7.setPriority(Priority.MEDIUM);
            t7.setReasonRecommendation("Hands-on SQL practice with grouped subqueries.");
            t7.setStatus(TaskStatus.PENDING);
            sampleTasks.add(t7);

            studyTaskRepository.saveAll(sampleTasks);
            logger.info("Initial demo student account and study plan seeded successfully.");
        }
    }

    private void createTopic(Subject subject, String name, String desc, Difficulty diff, int mins, TopicStatus status) {
        Topic topic = new Topic();
        topic.setSubject(subject);
        topic.setName(name);
        topic.setDescription(desc);
        topic.setDifficulty(diff);
        topic.setEstimatedMinutes(mins);
        topic.setStatus(status);
        topicRepository.save(topic);
    }
}
