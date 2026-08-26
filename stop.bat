@echo off
rem STOP SCRIPT: finds and terminates the application listening on port 8080.
setlocal

set "FOUND="
for /f "tokens=5" %%P in ('netstat -ano ^| findstr ":8080" ^| findstr "LISTENING"') do set "FOUND=%%P"
for /f "tokens=5" %%P in ('netstat -ano ^| findstr ":8080" ^| findstr "LISTENING"') do taskkill /PID %%P /F >nul 2>&1

if defined FOUND (
    echo Online Exam Portal stopped. PID %FOUND% was terminated.
) else (
    echo Online Exam Portal is not running on port 8080.
)
pause
