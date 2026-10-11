param (
    [ValidateSet("start", "stop", "status", "restart")]
    [string]$Action = "status",
    [int]$Port = 8080,
    [string]$Profile = "dev"
)

$JarPath = "target\placement-management-system-1.0.0.jar"
$LogPath = "server.log"

function Check-ServerStatus {
    $conn = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue
    if ($conn) {
        $proc = Get-Process -Id $conn.OwningProcess -ErrorAction SilentlyContinue
        Write-Host "[STATUS] Application is RUNNING on port $Port (Process ID: $($conn.OwningProcess), Name: $($proc.ProcessName))" -ForegroundColor Green
        return $conn.OwningProcess
    } else {
        Write-Host "[STATUS] Application is STOPPED (Port $Port is free)" -ForegroundColor Yellow
        return $null
    }
}

switch ($Action) {
    "status" {
        Check-ServerStatus
    }
    "stop" {
        $pidToKill = Check-ServerStatus
        if ($pidToKill) {
            Write-Host "[ACTION] Stopping process ID $pidToKill..." -ForegroundColor Cyan
            Stop-Process -Id $pidToKill -Force
            Start-Sleep -Seconds 2
            Write-Host "[OK] Server stopped successfully." -ForegroundColor Green
        } else {
            Write-Host "[INFO] No running server found on port $Port." -ForegroundColor Gray
        }
    }
    "start" {
        $existingPid = Check-ServerStatus
        if ($existingPid) {
            Write-Host "[WARN] Server is already running (PID: $existingPid). Use -Action restart to restart." -ForegroundColor Yellow
            return
        }

        if (-not (Test-Path $JarPath)) {
            Write-Host "[BUILD] JAR not found. Packaging with Maven..." -ForegroundColor Cyan
            $mvnCmd = "$env:USERPROFILE\.maven\bin\mvn.cmd"
            if (Test-Path $mvnCmd) {
                & $mvnCmd clean package -DskipTests
            } else {
                mvn clean package -DskipTests
            }
        }

        Write-Host "[ACTION] Starting application on port $Port (Profile: $Profile)..." -ForegroundColor Cyan
        $startInfo = New-Object System.Diagnostics.ProcessStartInfo
        $startInfo.FileName = "java"
        $startInfo.Arguments = "-Dserver.port=$Port -Dspring.profiles.active=$Profile -jar $JarPath"
        $startInfo.RedirectStandardOutput = $true
        $startInfo.RedirectStandardError = $true
        $startInfo.UseShellExecute = $false
        $startInfo.CreateNoWindow = $true

        $process = [System.Diagnostics.Process]::Start($startInfo)
        Write-Host "[OK] Process started with PID: $($process.Id)" -ForegroundColor Green
        Write-Host "[INFO] Point your browser to http://localhost:$Port/" -ForegroundColor Cyan
    }
    "restart" {
        & $PSCommandPath -Action stop -Port $Port
        Start-Sleep -Seconds 2
        & $PSCommandPath -Action start -Port $Port -Profile $Profile
    }
}
