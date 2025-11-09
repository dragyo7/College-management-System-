# Initialize strict mode
Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

# Functions
function Test-MySQLConnection {
    try {
        $process = Get-Process "mysqld" -ErrorAction SilentlyContinue
        return $null -ne $process
    } catch {
        return $false
    }
}

function New-OutputDirs {
    [CmdletBinding()]
    param()
    
    Write-Host "Creating output directories..." -ForegroundColor Yellow
    @("out", "out/classes") | ForEach-Object {
        if (-not (Test-Path $_)) {
            New-Item -ItemType Directory -Path $_ -Force | Out-Null
        }
    }
}

function Build-Project {
    [CmdletBinding()]
    param()
    
    Write-Host "Compiling Java files..." -ForegroundColor Yellow
    $classpath = "lib/mysql-connector-j-9.5.0/*;lib/jakarta-servlet-api-6.0.0/*;."
    $javaFiles = Get-ChildItem -Path "src" -Filter "*.java" -Recurse | 
                 Select-Object -ExpandProperty FullName
    & javac -d out/classes -cp $classpath $javaFiles
    if ($LASTEXITCODE -ne 0) {
        throw "Compilation failed!"
    }
}

function Start-Build {
    [CmdletBinding()]
    param()
    
    Write-Host "🎓 Building College Admission Management System..." -ForegroundColor Cyan

    if (-not (Test-MySQLConnection)) {
        Write-Host "❌ MySQL is not running! Please start MySQL and try again." -ForegroundColor Red
        return 1
    }

    New-OutputDirs

    Write-Host "Initializing database..." -ForegroundColor Yellow
    & mysql -u root -pMysql123@## -e "source database/schema.sql"
    if ($LASTEXITCODE -ne 0) {
        throw "Failed to initialize database!"
    }

    Build-Project

    Write-Host "✅ Build completed successfully!" -ForegroundColor Green
    Write-Host "Run 'java -cp ""out/classes;lib/mysql-connector-j-9.5.0/*"" main.Main' to start the application" -ForegroundColor Yellow
    return 0
}

# Execute build script
try {
    exit (Start-Build)
} catch {
    Write-Host "❌ Build failed: $_" -ForegroundColor Red
    exit 1
}