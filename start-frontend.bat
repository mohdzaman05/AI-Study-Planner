@echo off
setlocal enabledelayedexpansion

echo ===================================================
echo     AI Study Planner - Frontend Launcher
echo ===================================================
echo.

cd /d "%~dp0"

echo Opening AI Study Planner in your default browser...
echo.
echo Note: Ensure the backend is running on http://localhost:8080
echo (Run start-backend.bat first if you haven't already).
echo.

:: Open either the Spring Boot served web address or direct frontend file
start "" "http://localhost:8080/index.html"

echo Browser launched to http://localhost:8080/index.html
echo (If backend is not yet started, you can also open "%~dp0frontend\index.html" directly)
echo.
timeout /t 3 >nul
