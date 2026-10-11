@echo off
setlocal enabledelayedexpansion

echo ==============================================================
echo College Placement Management System - Deployment Script
echo ==============================================================

:: Check Java
where java >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Java is not found in PATH. Please install Java 17 or Java 21.
    pause
    exit /b 1
)

:: Check if JAR exists; if not, build it with Maven
set JAR_FILE=target\placement-management-system-1.0.0.jar
if not exist "%JAR_FILE%" (
    echo [INFO] Application JAR not found. Building with Maven...
    if exist "%USERPROFILE%\.maven\bin\mvn.cmd" (
        call "%USERPROFILE%\.maven\bin\mvn.cmd" clean package -DskipTests
    ) else (
        where mvn >nul 2>&1
        if %ERRORLEVEL% equ 0 (
            call mvn clean package -DskipTests
        ) else (
            echo [ERROR] Maven not found. Cannot package application.
            pause
            exit /b 1
        )
    )
)

if not exist "%JAR_FILE%" (
    echo [ERROR] Build failed. JAR file not found.
    pause
    exit /b 1
)

:: Set upload directory
if not exist "uploads\resumes" (
    mkdir "uploads\resumes"
)

echo [INFO] Starting College Placement Management System on port 8080...
echo [INFO] Profile: dev (H2 MySQL-compatible persistent database)
echo [INFO] Access URL: http://localhost:8080/
echo [INFO] Press Ctrl+C to stop the server.
echo ==============================================================

java -Xms256m -Xmx1024m -jar "%JAR_FILE%"
