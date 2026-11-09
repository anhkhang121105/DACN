$ErrorActionPreference = 'SilentlyContinue'

function Stop-Port([int]$port) {
  $conns = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
  foreach ($c in $conns) {
    Stop-Process -Id $c.OwningProcess -Force -ErrorAction SilentlyContinue
  }
}

Stop-Port 5173
Stop-Port 8080
Get-CimInstance Win32_Process -Filter "Name='mysqld.exe'" |
  Where-Object { $_.CommandLine -match 'pcgv-mysql-data' } |
  ForEach-Object { Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue }

Write-Host 'Da tat web (frontend, backend, MySQL 3307).'
