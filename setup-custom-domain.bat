@echo off
:: Batch script to map placement.college.edu and placement.local to 127.0.0.1
echo ==============================================================
echo Custom Domain Mapping for College Placement Portal
echo ==============================================================

net session >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Please right-click this file and select "Run as administrator".
    echo Administrative privileges are required to update the hosts file.
    pause
    exit /b 1
)

set HOSTS_FILE=%WINDIR%\System32\drivers\etc\hosts

findstr /C:"placement.college.edu" "%HOSTS_FILE%" >nul 2>&1
if %ERRORLEVEL% equ 0 (
    echo [INFO] Domain 'placement.college.edu' is already mapped in hosts file.
) else (
    echo 127.0.0.1 placement.college.edu>> "%HOSTS_FILE%"
    echo [OK] Added: 127.0.0.1 placement.college.edu
)

findstr /C:"placement.local" "%HOSTS_FILE%" >nul 2>&1
if %ERRORLEVEL% equ 0 (
    echo [INFO] Domain 'placement.local' is already mapped in hosts file.
) else (
    echo 127.0.0.1 placement.local>> "%HOSTS_FILE%"
    echo [OK] Added: 127.0.0.1 placement.local
)

ipconfig /flushdns >nul
echo.
echo [SUCCESS] Domains mapped successfully.
echo You can now access the portal at:
echo   - http://placement.college.edu:8080/
echo   - http://placement.local:8080/
echo ==============================================================
pause
