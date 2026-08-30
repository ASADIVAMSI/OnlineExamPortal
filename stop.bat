@echo off
rem STOP SCRIPT: finds and terminates the backend on 8080 and the frontend on 5173.
setlocal

set "BACKEND_FOUND="
set "FRONTEND_FOUND="

for /f "tokens=5" %%P in ('netstat -ano ^| findstr ":8080" ^| findstr "LISTENING"') do set "BACKEND_FOUND=%%P"
for /f "tokens=5" %%P in ('netstat -ano ^| findstr ":8080" ^| findstr "LISTENING"') do taskkill /PID %%P /F >nul 2>&1

for /f "tokens=5" %%P in ('netstat -ano ^| findstr ":5173" ^| findstr "LISTENING"') do set "FRONTEND_FOUND=%%P"
for /f "tokens=5" %%P in ('netstat -ano ^| findstr ":5173" ^| findstr "LISTENING"') do taskkill /PID %%P /F >nul 2>&1

if defined BACKEND_FOUND (
    echo Backend stopped. PID %BACKEND_FOUND% was terminated.
) else (
    echo Backend was not running on port 8080.
)

if defined FRONTEND_FOUND (
    echo Frontend stopped. PID %FRONTEND_FOUND% was terminated.
) else (
    echo Frontend was not running on port 5173.
)

pause
