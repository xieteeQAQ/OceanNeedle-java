@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

rem ============================================================
rem  OceanNeedle-java - Windows launcher
rem
rem  Works in both layouts:
rem    dist\OceanNeedle-win64\   jar and lib side by side (packaged)
rem    project root              jar under target\ (source tree)
rem
rem  IMPORTANT: keep this file pure ASCII. Chinese text inside a .bat
rem  breaks cmd.exe parsing (see build.bat for details).
rem ============================================================

set "JAR="
set "LIBDIR="

rem layout 1: packaged folder
for %%f in ("%~dp0*.jar") do if not defined JAR (
    set "JAR=%%~ff"
    set "LIBDIR=%~dp0lib"
)

rem layout 2: source tree
if not defined JAR if exist "%~dp0target" (
    for %%f in ("%~dp0target\*.jar") do if not defined JAR (
        set "JAR=%%~ff"
        set "LIBDIR=%~dp0target\lib"
    )
)

if not defined JAR goto no_jar
if not exist "!JAR!" goto no_jar

if not exist "!LIBDIR!" (
    echo [ERROR] dependency folder not found: !LIBDIR!
    echo         Run build.bat first.
    pause
    exit /b 1
)

rem The current directory goes FIRST on the classpath, so dist\jdbc.properties
rem overrides the copy packed inside the jar: you can change the database
rem password without rebuilding.
java -cp ".;!JAR!;!LIBDIR!\*" com.xietee.Main %*
set "RC=%errorlevel%"
if "%RC%"=="0" exit /b 0

echo.
echo Program exited with code %RC% - see logs\app.log in this folder for details.
pause
exit /b %RC%

:no_jar
echo [ERROR] OceanNeedle-java-*.jar not found.
echo         Run build.bat first, or run "mvn package".
pause
exit /b 1
