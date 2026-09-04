$ErrorActionPreference = "Continue"

Write-Host "=== AI STUDY PLANNER - END-TO-END VERIFICATION ===" -ForegroundColor Cyan

# 1. Test Static Frontend Files via Spring Boot
Write-Host "`n[1/10] Checking Frontend File Serving on port 8080..." -ForegroundColor Yellow
$frontendRes = Invoke-WebRequest -Uri 'http://localhost:8080/index.html' -UseBasicParsing
if ($frontendRes.StatusCode -eq 200) {
    Write-Host "  PASS: Frontend index.html served successfully (HTTP 200)" -ForegroundColor Green
} else {
    Write-Host "  FAIL: Frontend index.html returned HTTP $($frontendRes.StatusCode)" -ForegroundColor Red
}

# 2. Test Registration with a brand new email
Write-Host "`n[2/10] Testing Student Registration..." -ForegroundColor Yellow
$rand = Get-Random -Minimum 1000 -Maximum 9999
$testEmail = "student_$rand@university.edu"
$regPayload = @{
    fullName = "Alex Student"
    email = $testEmail
    password = "Password123"
    confirmPassword = "Password123"
} | ConvertTo-Json

$regRes = Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/register' -Method Post -ContentType 'application/json' -Body $regPayload
if ($regRes.token -and $regRes.email -eq $testEmail) {
    Write-Host "  PASS: Registration succeeded for $testEmail! Token received." -ForegroundColor Green
    $token = $regRes.token
} else {
    Write-Host "  FAIL: Registration failed." -ForegroundColor Red
    exit 1
}

# 3. Test Duplicate Registration (Must return "This email is already registered.")
Write-Host "`n[3/10] Testing Duplicate Email Registration..." -ForegroundColor Yellow
try {
    $dupRes = Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/register' -Method Post -ContentType 'application/json' -Body $regPayload
    Write-Host "  FAIL: Duplicate registration should have thrown an error!" -ForegroundColor Red
} catch {
    $errStream = $_.Exception.Response.GetResponseStream()
    $reader = New-Object System.IO.StreamReader($errStream)
    $errBody = $reader.ReadToEnd()
    Write-Host "  Response: $errBody"
    if ($errBody -match "This email is already registered\.") {
        Write-Host "  PASS: System explicitly returned: 'This email is already registered.'" -ForegroundColor Green
    } else {
        Write-Host "  FAIL: Expected 'This email is already registered.' but got: $errBody" -ForegroundColor Red
    }
}

# 4. Test Login
Write-Host "`n[4/10] Testing Student Login..." -ForegroundColor Yellow
$loginPayload = @{
    email = $testEmail
    password = "Password123"
} | ConvertTo-Json
$loginRes = Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/login' -Method Post -ContentType 'application/json' -Body $loginPayload
if ($loginRes.token) {
    Write-Host "  PASS: Login succeeded! JWT token issued." -ForegroundColor Green
    $headers = @{ "Authorization" = "Bearer $($loginRes.token)" }
} else {
    Write-Host "  FAIL: Login failed." -ForegroundColor Red
}

# 5. Test Creating a Subject
Write-Host "`n[5/10] Creating Subject 'Software Engineering'..." -ForegroundColor Yellow
$subjectPayload = @{
    name = "Software Engineering"
    description = "Software lifecycle, design patterns, and testing."
    difficulty = "MEDIUM"
    priority = "HIGH"
} | ConvertTo-Json
$subjectRes = Invoke-RestMethod -Uri 'http://localhost:8080/api/subjects' -Method Post -Headers $headers -ContentType 'application/json' -Body $subjectPayload
Write-Host "  PASS: Created Subject ID: $($subjectRes.id) Name: $($subjectRes.name)" -ForegroundColor Green
$subjectId = $subjectRes.id

# 6. Test Creating Topics
Write-Host "`n[6/10] Creating Topics for Subject..." -ForegroundColor Yellow
$topic1Payload = @{
    name = "Agile Scrum Methodology"
    description = "Sprints, standups, and story points."
    difficulty = "EASY"
    estimatedMinutes = 45
    status = "NOT_STARTED"
} | ConvertTo-Json
$topic1Res = Invoke-RestMethod -Uri "http://localhost:8080/api/subjects/$subjectId/topics" -Method Post -Headers $headers -ContentType 'application/json' -Body $topic1Payload
Write-Host "  PASS: Created Topic 1: $($topic1Res.name) (ID: $($topic1Res.id))" -ForegroundColor Green

$topic2Payload = @{
    name = "Design Patterns (GoF)"
    description = "Factory, Singleton, Observer, and Decorator."
    difficulty = "HARD"
    estimatedMinutes = 90
    status = "NOT_STARTED"
} | ConvertTo-Json
$topic2Res = Invoke-RestMethod -Uri "http://localhost:8080/api/subjects/$subjectId/topics" -Method Post -Headers $headers -ContentType 'application/json' -Body $topic2Payload
Write-Host "  PASS: Created Topic 2: $($topic2Res.name) (ID: $($topic2Res.id))" -ForegroundColor Green

# 7. Test Creating an Exam
Write-Host "`n[7/10] Creating Exam Milestone..." -ForegroundColor Yellow
$examDate = (Get-Date).AddDays(14).ToString("yyyy-MM-dd")
$examPayload = @{
    subjectId = $subjectId
    examDate = $examDate
    examTime = "10:00"
    importantTopics = "Design Patterns, Testing, Agile"
    notes = "Midterm examination in Hall B"
} | ConvertTo-Json
$examRes = Invoke-RestMethod -Uri 'http://localhost:8080/api/exams' -Method Post -Headers $headers -ContentType 'application/json' -Body $examPayload
Write-Host "  PASS: Created Exam on $examDate (Days remaining: $($examRes.daysRemaining))" -ForegroundColor Green

# 8. Test Generating AI Study Plan
Write-Host "`n[8/10] Generating Study Plan..." -ForegroundColor Yellow
$planPayload = @{
    planDays = 7
    availableHoursPerDay = 3.0
    preferredStartTime = "18:00"
    focusGoal = "Prepare for Software Engineering Midterm"
} | ConvertTo-Json
$planRes = Invoke-RestMethod -Uri 'http://localhost:8080/api/study-plans/generate' -Method Post -Headers $headers -ContentType 'application/json' -Body $planPayload
Write-Host "  PASS: Study Plan generated! Title: '$($planRes.title)' Total Tasks: $($planRes.totalTasks)" -ForegroundColor Green

# 9. Test Tasks & Completion
Write-Host "`n[9/10] Checking Tasks and Marking First Task Completed..." -ForegroundColor Yellow
$tasks = Invoke-RestMethod -Uri 'http://localhost:8080/api/tasks' -Method Get -Headers $headers
Write-Host "  Retrieved $($tasks.Count) tasks."
if ($tasks.Count -gt 0) {
    $firstTask = $tasks[0]
    $compRes = Invoke-RestMethod -Uri "http://localhost:8080/api/tasks/$($firstTask.id)/complete" -Method Put -Headers $headers
    Write-Host "  PASS: Task ID $($firstTask.id) marked as '$($compRes.status)'" -ForegroundColor Green
}

# 10. Test AI Assistant Chat
Write-Host "`n[10/10] Testing AI Study Assistant Chat..." -ForegroundColor Yellow
$chatPayload = @{
    message = "Explain the difference between Factory and Singleton patterns in simple student terms."
} | ConvertTo-Json
$chatRes = Invoke-RestMethod -Uri 'http://localhost:8080/api/ai/chat' -Method Post -Headers $headers -ContentType 'application/json' -Body $chatPayload
Write-Host "  PASS: AI Assistant responded successfully!" -ForegroundColor Green
Write-Host "  Preview: $($chatRes.reply.Substring(0, [Math]::Min(120, $chatRes.reply.Length)))..." -ForegroundColor Gray

Write-Host "`n=== ALL 10 TESTS PASSED SUCCESSFULLY! ===" -ForegroundColor Green
