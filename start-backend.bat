@echo off
setlocal enabledelayedexpansion

echo ===================================================
echo     AI Study Planner - Backend Startup Script
echo ===================================================
echo.

cd /d "%~dp0"

:: 1. Detect Java 17+
set "JAVA_CMD="

:: Check known Adoptium JDK 17 installation
if exist "C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot\bin\java.exe" (
    set "JAVA_CMD=C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot\bin\java.exe"
    goto found_java
)

:: Search Adoptium directory if version folder differs
for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk-17*") do (
    if exist "%%D\bin\java.exe" (
        set "JAVA_CMD=%%D\bin\java.exe"
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
    pause
    exit /b 1
)

echo [1/3] Using Java runtime:
echo       "%JAVA_CMD%"
"%JAVA_CMD%" -version
echo.

:: 2. Check backend JAR
set "JAR_FILE=%~dp0backend\target\ai-study-planner-backend-1.0.0.jar"
if not exist "%JAR_FILE%" (
    echo [2/3] Backend JAR not found at "%JAR_FILE%".
    echo Building backend with Maven...
    cd /d "%~dp0backend"
    set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot"
    set "PATH=%JAVA_HOME%\bin;C:\Users\ASUS\.maven\apache-maven-3.9.9\bin;%PATH%"
    call mvn clean package -DskipTests
    cd /d "%~dp0"
    if not exist "%JAR_FILE%" (
        echo [ERROR] Maven build failed.
        pause
        exit /b 1
    )
) else (
    echo [2/3] Backend JAR verified at:
    echo       "%JAR_FILE%"
)
echo.

:: 3. Run Backend
echo [3/3] Starting AI Study Planner Spring Boot Backend on http://localhost:8080 ...
echo Database: Embedded H2 in MySQL mode (persisted to ./data/studyplanner)
echo Press Ctrl+C at any time to stop the server.
echo.

"%JAVA_CMD%" -jar "%JAR_FILE%"

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] Backend stopped with an error (exit code: %ERRORLEVEL%).
    pause
)
