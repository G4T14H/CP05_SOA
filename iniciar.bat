@echo off
cd /d "%~dp0"
if exist release\autoescola-api.jar (
 java -jar release\autoescola-api.jar
) else (
 if not exist target\autoescola-api-1.0.0.jar call mvnw.cmd clean verify
 if errorlevel 1 exit /b 1
 java -jar target\autoescola-api-1.0.0.jar
)
pause
