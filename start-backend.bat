@echo off
setlocal enabledelayedexpansion

echo ===================================================
echo     AI Study Planner - Unified Startup Script
echo ===================================================
echo.

cd /d "%~dp0"

:: 1. Detect Java 17+
set "JAVA_CMD="

:: Check known Adoptium JDK 17 installation
if exist "C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot\bin\java.exe" (
    set "JAVA_CMD=C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot\bin\java.exe"
    set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot"
    goto found_java
)

:: Search Adoptium directory if version folder differs
for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk-17*") do (
    if exist "%%D\bin\java.exe" (
        set "JAVA_CMD=%%D\bin\java.exe"
        set "JAVA_HOME=%%D"
        goto found_java
    )
)

:: Check JAVA_HOME
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
        goto found_java
    )
)

:: Check default PATH java
for /f "tokens=*" %%A in ('where java 2^>nul') do (
    set "JAVA_CMD=%%A"
    goto found_java
)

:found_java
if not defined JAVA_CMD (
    echo [ERROR] No Java installation found!
    echo Please install JDK 17 or set JAVA_HOME to your JDK 17 folder.
    echo.
    pause
    exit /b 1
)

if defined JAVA_HOME (
    set "PATH=%JAVA_HOME%\bin;%PATH%"
)

echo [1/3] Java Runtime:
echo       "%JAVA_CMD%"
"%JAVA_CMD%" -version
echo.

:: 2. Check Maven & Backend JAR
set "JAR_FILE=%~dp0backend\target\ai-study-planner-backend-1.0.0.jar"

if not exist "%JAR_FILE%" (
    echo [2/3] Backend JAR not found at:
    echo       "%JAR_FILE%"
    echo Building backend with Maven...
    
    :: Add Maven to PATH if available in local user folder
    if exist "C:\Users\ASUS\.maven\apache-maven-3.9.9\bin" (
        set "PATH=C:\Users\ASUS\.maven\apache-maven-3.9.9\bin;%PATH%"
    )
    
    cd /d "%~dp0backend"
    call mvn clean package -DskipTests
    cd /d "%~dp0"
    
    if not exist "%JAR_FILE%" (
        echo.
        echo [ERROR] Maven build failed.
        pause
        exit /b 1
    )
) else (
    echo [2/3] Backend JAR verified at:
    echo       "%JAR_FILE%"
)
echo.

:: 3. Launch Health Monitor and Browser Launcher in Background
echo [3/3] Launching AI Study Planner on http://localhost:8080/ ...
echo Database: File-persisted H2 in MySQL mode (./backend/data/studyplanner)
echo Waiting for server health check before opening browser...
echo.

start "" /B powershell -NoProfile -Command "for ($i = 0; $i -lt 45; $i++) { Start-Sleep -Seconds 1; try { $res = Invoke-RestMethod -Uri 'http://localhost:8080/api/health' -TimeoutSec 2; if ($res.status -eq 'UP') { Write-Host '==================================================='; Write-Host 'AI Study Planner backend started on http://localhost:8080'; Write-Host 'Opening application in default browser...'; Write-Host '==================================================='; Start-Process 'http://localhost:8080/'; break } } catch {} }"

:: 4. Start Spring Boot in foreground so logs remain visible
"%JAVA_CMD%" -jar "%JAR_FILE%"

echo.
echo ===================================================
echo Spring Boot process has ended (Exit Code: %ERRORLEVEL%).
echo ===================================================
pause
