@REM ----------------------------------------------------------------------------
@REM Maven Wrapper Batch Script for Digital Stokvel
@REM ----------------------------------------------------------------------------
@echo off
setlocal enabledelayedexpansion

if "%JAVA_HOME%"=="" (
    if exist "D:\Program Files\Java\jdk-25.0.4" (
        set "JAVA_HOME=D:\Program Files\Java\jdk-25.0.4"
    )
)

set "DIR=%~dp0"
set "MAVEN_VERSION=3.9.9"
set "MAVEN_DIR=%DIR%.mvn\apache-maven-%MAVEN_VERSION%"
set "MAVEN_ZIP=%DIR%.mvn\apache-maven-%MAVEN_VERSION%-bin.zip"
set "MAVEN_CMD=%MAVEN_DIR%\bin\mvn.cmd"

if exist "%MAVEN_CMD%" goto runMaven

echo [INFO] Maven not found locally. Downloading Apache Maven %MAVEN_VERSION%...
if not exist "%DIR%.mvn" mkdir "%DIR%.mvn"

powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; $ProgressPreference = 'SilentlyContinue'; Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip' -OutFile '%MAVEN_ZIP%'"
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Failed to download Maven %MAVEN_VERSION%.
    exit /b %ERRORLEVEL%
)

echo [INFO] Extracting Maven...
powershell -Command "$ProgressPreference = 'SilentlyContinue'; Expand-Archive -Path '%MAVEN_ZIP%' -DestinationPath '%DIR%.mvn' -Force"
del "%MAVEN_ZIP%"

if not exist "%MAVEN_CMD%" (
    echo [ERROR] Maven installation failed, %MAVEN_CMD% not found.
    exit /b 1
)

:runMaven
call "%MAVEN_CMD%" %*
exit /b %ERRORLEVEL%
