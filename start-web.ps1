$ErrorActionPreference = 'Stop'

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$backend = Join-Path $root 'backend'
$frontend = Join-Path $root 'frontend'
$logs = Join-Path $root 'logs'
$jar = Join-Path $backend 'target\phancong-gv-1.0.0.jar'
$mysqlCli = (Get-Command mysql.exe -ErrorAction SilentlyContinue).Source
if (-not $mysqlCli) {
  $mysqlCli = @(
  'C:\xampp\mysql\bin\mysql.exe',
    'C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe',
    'C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe'
  ) | Where-Object { Test-Path $_ } | Select-Object -First 1
}

$dbHost = if ($env:DB_HOST) { $env:DB_HOST } else { '127.0.0.1' }
$dbPort = if ($env:DB_PORT) { [int]$env:DB_PORT } else { 3306 }
$dbName = if ($env:DB_NAME) { $env:DB_NAME } else { 'phancong_gv' }
$dbUser = if ($env:DB_USERNAME) { $env:DB_USERNAME } else { 'root' }

New-Item -ItemType Directory -Force -Path $logs | Out-Null

function Test-Port([string]$host, [int]$port) {
  $client = New-Object System.Net.Sockets.TcpClient
  try {
    $client.Connect($host, $port)
    return $true
  } catch {
    return $false
  } finally {
    $client.Dispose()
  }
}

function Wait-Port([string]$host, [int]$port, [int]$seconds) {
  for ($i = 0; $i -lt $seconds; $i++) {
    if (Test-Port $host $port) { return $true }
    Start-Sleep -Seconds 1
  }
  return $false
}

if (-not $mysqlCli) {
  throw 'Khong tim thay mysql.exe. Hay cai MySQL/XAMPP va them thu muc bin vao PATH.'
}

if (-not (Test-Port $dbHost $dbPort)) {
  throw "Khong ket noi duoc MySQL tai ${dbHost}:${dbPort}. Hay bat MySQL hoac dat DB_HOST/DB_PORT."
}

$passwords = if ($null -ne $env:DB_PASSWORD) { @($env:DB_PASSWORD) } else { @('', 'root') }
$connectedPassword = $null
foreach ($password in $passwords) {
  & $mysqlCli --protocol=tcp --host=$dbHost --port=$dbPort --user=$dbUser "--password=$password" -e 'SELECT 1;' 2>$null | Out-Null
  if ($LASTEXITCODE -eq 0) {
    $connectedPassword = $password
    break
  }
}
if ($null -eq $connectedPassword) {
  throw 'Khong dang nhap duoc MySQL. Dat DB_USERNAME va DB_PASSWORD roi chay lai.'
}

& $mysqlCli --protocol=tcp --host=$dbHost --port=$dbPort --user=$dbUser "--password=$connectedPassword" -e "CREATE DATABASE IF NOT EXISTS $dbName CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>$null | Out-Null
if ($LASTEXITCODE -ne 0) { throw "Khong tao duoc database $dbName." }

if (-not (Test-Port '127.0.0.1' 8080)) {
  Write-Host 'Starting backend on port 8080...'
  if (-not (Test-Path $jar)) {
    Write-Host 'Building backend...'
    if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) { throw 'Khong tim thay Maven. Hay cai Maven hoac dat mvn vao PATH.' }
    Push-Location $backend
    & mvn -DskipTests package
    Pop-Location
    if ($LASTEXITCODE -ne 0 -or -not (Test-Path $jar)) { throw "Build backend that bai: $jar" }
  }
  if (-not $env:DB_URL) {
    $env:DB_URL = "jdbc:mysql://${dbHost}:${dbPort}/${dbName}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=UTF-8"
  }
  $env:DB_USERNAME = $dbUser
  $env:DB_PASSWORD = $connectedPassword
  if (-not (Get-Command java -ErrorAction SilentlyContinue)) { throw 'Khong tim thay Java. Hay cai JDK 21 va dat java vao PATH.' }
  Start-Process -FilePath 'java' -ArgumentList "-jar `"$jar`"" -WorkingDirectory $backend `
    -RedirectStandardOutput (Join-Path $logs 'backend.log') `
    -RedirectStandardError (Join-Path $logs 'backend.err') -WindowStyle Hidden
  if (-not (Wait-Port '127.0.0.1' 8080 120)) {
    throw "Backend khong khoi dong duoc. Xem $(Join-Path $logs 'backend.err')"
  }
}

if (-not (Test-Port '127.0.0.1' 5173)) {
  Write-Host 'Starting frontend on port 5173...'
  if (-not (Get-Command npm -ErrorAction SilentlyContinue)) { throw 'Khong tim thay npm. Hay cai Node.js va dat npm vao PATH.' }
  if (-not (Test-Path (Join-Path $frontend 'node_modules'))) {
    Write-Host 'Installing frontend dependencies...'
    Push-Location $frontend
    & npm install
    $npmExitCode = $LASTEXITCODE
    Pop-Location
    if ($npmExitCode -ne 0) { throw 'Cai frontend dependencies that bai.' }
  }
  Start-Process -FilePath 'cmd.exe' -ArgumentList '/c "npm run dev -- --host 127.0.0.1 --port 5173"' `
    -WorkingDirectory $frontend `
    -RedirectStandardOutput (Join-Path $logs 'frontend.log') `
    -RedirectStandardError (Join-Path $logs 'frontend.err') -WindowStyle Hidden
  if (-not (Wait-Port '127.0.0.1' 5173 90)) {
    throw "Frontend khong khoi dong duoc. Xem $(Join-Path $logs 'frontend.err')"
  }
}

Write-Host ''
Write-Host 'Web san sang: http://localhost:5173'
Write-Host 'Dang nhap: trolydt / 123456'
Start-Process 'http://localhost:5173'
