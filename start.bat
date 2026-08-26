@echo off
rem START SCRIPT: checks port 8080, then launches the Spring Boot backend.
setlocal
cd /d "%~dp0"

for /f "tokens=5" %%P in ('netstat -ano ^| findstr ":8080" ^| findstr "LISTENING"') do goto already_running

if not exist "%USERPROFILE%\tools\apache-maven-3.9.10\bin\mvn.cmd" (
    echo Maven was not found at:
    echo %USERPROFILE%\tools\apache-maven-3.9.10\bin\mvn.cmd
    pause
    exit /b 1
)

set "MAVEN_HOME=%USERPROFILE%\tools\apache-maven-3.9.10"
set "PATH=%MAVEN_HOME%\bin;%PATH%"
echo Starting Online Exam Portal...
mvn -f backend\pom.xml spring-boot:run
exit /b %ERRORLEVEL%

:already_running
echo Online Exam Portal is already running on port 8080.
echo Open http://localhost:8080/index.html
pause
