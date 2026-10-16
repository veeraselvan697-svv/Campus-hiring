@echo off
title CampusHire Enterprise Server
echo ===================================================
echo   Starting CampusHire Placement Platform Backend
echo ===================================================

set JAVA_HOME=%~dp0tools\jdk-17
set JAVAC_CMD=%JAVA_HOME%\bin\javac.exe
set JAVA_CMD=%JAVA_HOME%\bin\java.exe

if not exist "%JAVA_CMD%" (
    echo [ERROR] Portable JDK not found in tools\jdk-17.
    pause
    exit /b 1
)

set CP=%~dp0backend\bin;%~dp0backend\lib\mysql-connector-j-8.3.0.jar;%~dp0backend\lib\gson-2.10.1.jar

echo Compiling Java sources...
if not exist "%~dp0backend\bin" mkdir "%~dp0backend\bin"

"%JAVAC_CMD%" -cp "%CP%" -d "%~dp0backend\bin" -encoding UTF-8 "%~dp0backend\src\com\campushire\CampusHireApplication.java" "%~dp0backend\src\com\campushire\config\*.java" "%~dp0backend\src\com\campushire\model\*.java" "%~dp0backend\src\com\campushire\dao\*.java" "%~dp0backend\src\com\campushire\controller\*.java" "%~dp0backend\src\com\campushire\util\*.java"

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

echo Starting CampusHire Server on http://localhost:8080 ...
"%JAVA_CMD%" -cp "%CP%" com.campushire.CampusHireApplication 8080
pause
