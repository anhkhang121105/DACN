$ErrorActionPreference = 'Stop'

$root = 'C:\pcgv'
if (-not (Test-Path $root)) {
  throw "Khong tim thay $root (junction toi project). Hay tao lai junction hoac bao admin."
}

$mysqlBase = 'C:\Program Files\MySQL\MySQL Server 8.0'
$mysqlData = 'D:\pcgv-mysql-data'
$logs = 'D:\pcgv-logs'
$mysqlExe = Join-Path $mysqlBase 'bin\mysqld.exe'
$mysqlCli = Join-Path $mysqlBase 'bin\mysql.exe'
$java = 'D:\IntelliJ IDEA Community Edition 2024.2.3\jbr\bin\java.exe'
$jar = Join-Path $root 'backend\app.jar'

New-Item -ItemType Directory -Force -Path $logs | Out-Null

function Test-Port([int]$port) {
  $client = New-Object System.Net.Sockets.TcpClient
  try {
    $client.Connect('127.0.0.1', $port)
    return $true
  } catch {
    return $false
  } finally {
    $client.Dispose()
  }
}

function Wait-Port([int]$port, [int]$seconds) {
  for ($i = 0; $i -lt $seconds; $i++) {
    if (Test-Port $port) { return $true }
    Start-Sleep -Seconds 1
  }
  return $false
}

if (-not (Test-Port 3307)) {
  Write-Host 'Starting MySQL on port 3307...'
  if (-not (Test-Path $mysqlData)) {
    New-Item -ItemType Directory -Force -Path $mysqlData | Out-Null
    & $mysqlExe --no-defaults --initialize-insecure --basedir="$mysqlBase" --datadir="$mysqlData" | Out-Null
  }
  $mysqlArgs = "--no-defaults --basedir=`"$mysqlBase`" --datadir=`"$mysqlData`" --port=3307 --bind-address=127.0.0.1"
  Start-Process -FilePath $mysqlExe -ArgumentList $mysqlArgs -WorkingDirectory 'D:\' `
    -RedirectStandardOutput (Join-Path $logs 'mysql.log') `
    -RedirectStandardError (Join-Path $logs 'mysql.err') -WindowStyle Hidden
  if (-not (Wait-Port 3307 60)) {
    throw 'MySQL khong khoi dong duoc. Xem D:\pcgv-logs\mysql.err'
  }
}

& $mysqlCli --no-defaults --host=127.0.0.1 --port=3307 --user=root -e "SELECT 1" 2>$null | Out-Null
if ($LASTEXITCODE -eq 0) {
  & $mysqlCli --no-defaults --host=127.0.0.1 --port=3307 --user=root -e "CREATE DATABASE IF NOT EXISTS phancong_gv CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci; ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';" | Out-Null
} else {
  & $mysqlCli --no-defaults --host=127.0.0.1 --port=3307 --user=root --password=root -e "CREATE DATABASE IF NOT EXISTS phancong_gv CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>$null | Out-Null
}

if (-not (Test-Port 8080)) {
  Write-Host 'Starting backend on port 8080...'
  if (-not (Test-Path $jar)) {
    throw "Thieu file $jar. Hay build backend truoc."
  }
  $env:DB_URL = 'jdbc:mysql://localhost:3307/phancong_gv?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=UTF-8'
  $env:DB_USERNAME = 'root'
  $env:DB_PASSWORD = 'root'
  if (-not (Test-Path $java)) { $java = 'java' }
  Start-Process -FilePath $java -ArgumentList "-jar `"$jar`"" -WorkingDirectory (Join-Path $root 'backend') `
    -RedirectStandardOutput (Join-Path $logs 'backend.log') `
    -RedirectStandardError (Join-Path $logs 'backend.err') -WindowStyle Hidden
  if (-not (Wait-Port 8080 120)) {
    throw 'Backend khong khoi dong duoc. Xem D:\pcgv-logs\backend.log'
  }
}

if (-not (Test-Port 5173)) {
  Write-Host 'Starting frontend on port 5173...'
  Start-Process -FilePath 'cmd.exe' -ArgumentList '/c "npm run dev -- --host 127.0.0.1 --port 5173"' `
    -WorkingDirectory (Join-Path $root 'frontend') `
    -RedirectStandardOutput (Join-Path $logs 'frontend.log') `
    -RedirectStandardError (Join-Path $logs 'frontend.err') -WindowStyle Hidden
  if (-not (Wait-Port 5173 90)) {
    throw 'Frontend khong khoi dong duoc. Xem D:\pcgv-logs\frontend.log'
  }
}

Write-Host ''
Write-Host 'Web san sang: http://localhost:5173'
Write-Host 'Dang nhap: trolydt / 123456'
Start-Process 'http://localhost:5173'
