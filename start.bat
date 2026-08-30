@echo off
rem START SCRIPT: launches the Spring Boot backend and the React/Vite frontend.
setlocal
cd /d "%~dp0"

set "BACKEND_PORT=8080"
set "FRONTEND_PORT=5173"
set "MAVEN_HOME=%USERPROFILE%\tools\apache-maven-3.9.10"
set "FRONTEND_DIR=%~dp0frontend"

for /f "tokens=5" %%P in ('netstat -ano ^| findstr ":%BACKEND_PORT%" ^| findstr "LISTENING"') do set "BACKEND_RUNNING=%%P"
for /f "tokens=5" %%P in ('netstat -ano ^| findstr ":%FRONTEND_PORT%" ^| findstr "LISTENING"') do set "FRONTEND_RUNNING=%%P"

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo Maven was not found at:
    echo %MAVEN_HOME%\bin\mvn.cmd
    pause
    exit /b 1
)

if not exist "%FRONTEND_DIR%\package.json" (
    echo Frontend package.json was not found at:
    echo %FRONTEND_DIR%\package.json
    pause
    exit /b 1
)

set "PATH=%MAVEN_HOME%\bin;%PATH%"

echo Building React frontend...
pushd "%FRONTEND_DIR%"
call npm run build
if errorlevel 1 (
    popd
    echo Frontend build failed.
    pause
    exit /b 1
)
popd

if not defined BACKEND_RUNNING (
    echo Starting Spring Boot backend on port %BACKEND_PORT%...
    start "Online Exam Portal Backend" /min cmd /c "cd /d ""%~dp0"" && mvn -f backend\pom.xml spring-boot:run"
) else (
    echo Backend already running on port %BACKEND_PORT% with PID %BACKEND_RUNNING%.
)

if not defined FRONTEND_RUNNING (
    echo Starting React frontend on port %FRONTEND_PORT%...
    start "Online Exam Portal Frontend" /min cmd /c "cd /d ""%FRONTEND_DIR%"" && npm run dev -- --host 0.0.0.0 --port %FRONTEND_PORT%"
) else (
    echo Frontend already running on port %FRONTEND_PORT% with PID %FRONTEND_RUNNING%.
)

echo Opening http://localhost:%FRONTEND_PORT%/ in your browser...
start "" "http://localhost:%FRONTEND_PORT%/"
exit /b 0
