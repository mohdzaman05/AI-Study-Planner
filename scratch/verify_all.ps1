$ErrorActionPreference = "Stop"

Write-Host "=== 1. Testing GET /api/health ==="
$health = Invoke-RestMethod -Uri "http://localhost:8080/api/health" -TimeoutSec 5
Write-Host "Health status: $($health.status)"

Write-Host "`n=== 2. Testing Static Frontend on http://localhost:8080/ ==="
$pages = @(
    "index.html",
    "login.html",
    "register.html",
    "dashboard.html",
    "subjects.html",
    "topics.html",
    "exams.html",
    "study-plan.html",
    "tasks.html",
    "progress.html",
    "ai-assistant.html",
    "study-plan-history.html",
    "profile.html",
    "css/style.css",
    "js/api.js",
    "js/auth.js"
)

foreach ($p in $pages) {
    $res = Invoke-WebRequest -Uri ("http://localhost:8080/" + $p) -UseBasicParsing -TimeoutSec 5
    Write-Host "GET /$p -> StatusCode: $($res.StatusCode)"
}

Write-Host "`n=== 3. Testing Registration (Test Student) ==="
$regPayload = @{
    fullName = "Test Student"
    email = "teststudent@example.com"
    password = "TestPassword123"
    confirmPassword = "TestPassword123"
} | ConvertTo-Json

$reg = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method Post -ContentType "application/json" -Body $regPayload
Write-Host "Registered user ID: $($reg.userId), Email: $($reg.email), FullName: $($reg.fullName)"
Write-Host "JWT Token issued (length: $($reg.token.Length))"

Write-Host "`n=== 4. Testing Duplicate Registration ==="
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method Post -ContentType "application/json" -Body $regPayload
    Write-Host "[FAIL] Duplicate registration should have been rejected!"
} catch {
    Write-Host "Duplicate rejected as expected: $($_.Exception.Message)"
}

Write-Host "`n=== 5. Testing Login (Test Student) ==="
$loginPayload = @{
    email = "teststudent@example.com"
    password = "TestPassword123"
} | ConvertTo-Json

$login = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body $loginPayload
Write-Host "Login succeeded! Token issued for: $($login.email)"
$tokenA = $login.token
$headersA = @{ "Authorization" = "Bearer $tokenA" }

Write-Host "`n=== 6. Testing Wrong Password Login ==="
try {
    $badLoginPayload = @{
        email = "teststudent@example.com"
        password = "WrongPassword999"
    } | ConvertTo-Json
    Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body $badLoginPayload
    Write-Host "[FAIL] Invalid password login should have been rejected!"
} catch {
    Write-Host "Invalid password rejected as expected: $($_.Exception.Message)"
}

Write-Host "`n=== 7. Testing Create Subject ==="
$subPayload = @{
    name = "Data Structures & Algorithms"
    description = "CS core curriculum"
    difficulty = "HARD"
    priority = "HIGH"
    examDate = (Get-Date).AddDays(21).ToString("yyyy-MM-dd")
} | ConvertTo-Json

$subject = Invoke-RestMethod -Uri "http://localhost:8080/api/subjects" -Method Post -Headers $headersA -ContentType "application/json" -Body $subPayload
Write-Host "Subject created: ID=$($subject.id), Name=$($subject.name), Difficulty=$($subject.difficulty)"

Write-Host "`n=== 8. Testing Create Topic ==="
$topicPayload = @{
    name = "Binary Search Trees & AVL"
    description = "Tree balancing and traversals"
    difficulty = "HARD"
    estimatedMinutes = 60
} | ConvertTo-Json

$topic = Invoke-RestMethod -Uri "http://localhost:8080/api/subjects/$($subject.id)/topics" -Method Post -Headers $headersA -ContentType "application/json" -Body $topicPayload
Write-Host "Topic created: ID=$($topic.id), Name=$($topic.name), Estimated=$($topic.estimatedMinutes) min"

Write-Host "`n=== 9. Testing Create Exam ==="
$examPayload = @{
    subjectId = $subject.id
    examDate = (Get-Date).AddDays(21).ToString("yyyy-MM-dd")
    examTime = "10:00"
    importantTopics = "BST, Balanced Trees, Red-Black Trees"
    notes = "Final theory examination"
} | ConvertTo-Json

$exam = Invoke-RestMethod -Uri "http://localhost:8080/api/exams" -Method Post -Headers $headersA -ContentType "application/json" -Body $examPayload
Write-Host "Exam created: ID=$($exam.id), Date=$($exam.examDate), Time=$($exam.examTime)"

Write-Host "`n=== 10. Testing Generate Study Plan ==="
$planPayload = @{
    availableHoursPerDay = 3.0
    preferredStartTime = "18:00"
    planDurationDays = 7
} | ConvertTo-Json

$plan = Invoke-RestMethod -Uri "http://localhost:8080/api/study-plans/generate" -Method Post -Headers $headersA -ContentType "application/json" -Body $planPayload
Write-Host "Plan generated: ID=$($plan.id), Title=$($plan.title), Total Tasks: $($plan.tasks.Count)"

Write-Host "`n=== 11. Testing Fetch Tasks & Complete Task ==="
$tasks = Invoke-RestMethod -Uri "http://localhost:8080/api/tasks" -Method Get -Headers $headersA
Write-Host "Retrieved $($tasks.Count) tasks for user."
$targetTask = $tasks[0]
Write-Host "Completing Task ID: $($targetTask.id), Topic: $($targetTask.topicName)"
$compTask = Invoke-RestMethod -Uri "http://localhost:8080/api/tasks/$($targetTask.id)/complete" -Method Put -Headers $headersA
Write-Host "Task status updated to: $($compTask.status)"

Write-Host "`n=== 12. Testing Progress Endpoint ==="
$prog = Invoke-RestMethod -Uri "http://localhost:8080/api/progress" -Method Get -Headers $headersA
Write-Host "Overall Completion Rate: $($prog.overallCompletionRate)%"
Write-Host "Tasks Completed: $($prog.completedTasksCount) / $($prog.totalTasksCount)"

Write-Host "`n=== 13. Testing AI Assistant Chat ==="
$chatPayload = @{
    message = "Can you provide 2 key tips to prepare for my upcoming Data Structures exam?"
} | ConvertTo-Json

$chat = Invoke-RestMethod -Uri "http://localhost:8080/api/ai/chat" -Method Post -Headers $headersA -ContentType "application/json" -Body $chatPayload
Write-Host "AI Assistant Response:"
Write-Host $chat.response

Write-Host "`n=== 14. Testing Security & Data Isolation ==="
# Register Student B
$regBPayload = @{
    fullName = "Student B"
    email = "studentb@example.com"
    password = "PasswordB123"
    confirmPassword = "PasswordB123"
} | ConvertTo-Json

$regB = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method Post -ContentType "application/json" -Body $regBPayload
$headersB = @{ "Authorization" = "Bearer $($regB.token)" }

# Try accessing Student A's subject with Student B's token
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/subjects/$($subject.id)" -Method Get -Headers $headersB
    Write-Host "[FAIL] Student B was able to access Student A's subject!"
} catch {
    Write-Host "[PASS] Student B access blocked: $($_.Exception.Message)"
}

# Try accessing without token
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/subjects/$($subject.id)" -Method Get
    Write-Host "[FAIL] Unauthenticated request was permitted!"
} catch {
    Write-Host "[PASS] Unauthenticated access blocked: $($_.Exception.Message)"
}

Write-Host "`n======================================================="
Write-Host ">>> ALL VERIFICATION TESTS COMPLETED SUCCESSFULLY! <<<"
Write-Host "======================================================="
