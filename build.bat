@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"
set "ROOT=%CD%"

rem ============================================================
rem  OceanNeedle-java - Windows 11 packaging script
rem  Usage: build.bat [--skip-tests] [--no-dist] [--no-zip] [--no-pause]
rem
rem  Output:
rem    target\OceanNeedle-java-1.0-SNAPSHOT.jar   executable jar
rem    target\lib\*.jar                           runtime dependencies
rem    dist\OceanNeedle-win64\                    ready-to-run folder
rem    dist\OceanNeedle-win64.zip                 zip package
rem
rem  IMPORTANT: keep this file pure ASCII.
rem  Chinese text inside a .bat breaks cmd.exe parsing (codepage and
rem  byte-offset desync), which shows up as lines like
rem  "'xx' is not recognized as an internal or external command".
rem ============================================================

set "SKIP_TESTS=0"
set "MAKE_DIST=1"
set "MAKE_ZIP=1"
set "NO_PAUSE=0"
set "JAVA="
set "MVN="
set "FAILMSG="

:parse_args
if "%~1"=="" goto args_done
if /i "%~1"=="--skip-tests" set "SKIP_TESTS=1"
if /i "%~1"=="--no-dist" set "MAKE_DIST=0"
if /i "%~1"=="--no-zip" set "MAKE_ZIP=0"
if /i "%~1"=="--no-pause" set "NO_PAUSE=1"
if /i "%~1"=="--help" goto usage
if /i "%~1"=="-h" goto usage
shift
goto parse_args
:args_done

echo ============================================================
echo   OceanNeedle-java  -  Windows 11 package build
echo ============================================================
echo.

rem ------------------------------------------------------------
rem 1. locate JDK (needs 21 or newer)
rem ------------------------------------------------------------
if defined JAVA_HOME (
    set "JAVA_CAND=%JAVA_HOME%"
    if "!JAVA_CAND:~-1!"=="\" set "JAVA_CAND=!JAVA_CAND:~0,-1!"
    if exist "!JAVA_CAND!\bin\java.exe" set "JAVA=!JAVA_CAND!\bin\java.exe"
)
if not defined JAVA (
    for /f "delims=" %%p in ('where java 2^>nul') do if not defined JAVA set "JAVA=%%p"
)
if not defined JAVA (
    set "FAILMSG=java.exe not found. Install a JDK 21+ and set JAVA_HOME, e.g. winget install EclipseAdoptium.Temurin.21.JDK"
    goto fail
)

"%JAVA%" -version > "%TEMP%\on_java_ver.txt" 2>&1
for /f "tokens=3" %%v in ('findstr /i "version" "%TEMP%\on_java_ver.txt"') do set "JAVA_RAW=%%v"
del /q "%TEMP%\on_java_ver.txt" >nul 2>nul
if defined JAVA_RAW set "JAVA_RAW=%JAVA_RAW:"=%"
set "JV1="
set "JV2="
for /f "tokens=1 delims=." %%a in ("%JAVA_RAW%") do set "JV1=%%a"
for /f "tokens=2 delims=." %%a in ("%JAVA_RAW%") do set "JV2=%%a"
if "%JV1%"=="1" (set "JAVA_MAJOR=%JV2%") else (set "JAVA_MAJOR=%JV1%")
if not defined JAVA_MAJOR set "JAVA_MAJOR=0"
if %JAVA_MAJOR% LSS 21 (
    set "FAILMSG=JDK %JAVA_RAW% is too old. This project needs JDK 21 or newer, e.g. winget install EclipseAdoptium.Temurin.21.JDK"
    goto fail
)

echo [JDK]     %JAVA%
echo [VERSION] %JAVA_RAW%
echo.

rem ------------------------------------------------------------
rem 2. locate Maven
rem ------------------------------------------------------------
call :detect_maven
if errorlevel 1 (
    set "FAILMSG=Maven (mvn) not found. Install it and set MAVEN_HOME, e.g. winget install Apache.Maven"
    goto fail
)
echo [MAVEN]   %MVN%
echo.

rem ------------------------------------------------------------
rem 3. check database config
rem    jdbc.properties is packaged INTO the jar, so it must exist
rem    and must not still be the placeholder template.
rem ------------------------------------------------------------
if not exist "src\main\resources\jdbc.properties" goto make_jdbc
findstr /b /c:"jdbc.password=<" "src\main\resources\jdbc.properties" >nul 2>nul
if not errorlevel 1 (
    set "FAILMSG=src\main\resources\jdbc.properties still has the placeholder password. Fill in the real one and run again"
    goto fail
)
echo [CONFIG]  src\main\resources\jdbc.properties  OK
echo.

rem ------------------------------------------------------------
rem 4. build
rem ------------------------------------------------------------
echo [1/3] mvn clean package ...
echo.
set "MVN_ARGS=-B clean package"
if "%SKIP_TESTS%"=="1" set "MVN_ARGS=-B clean package -DskipTests"
if /i "%MVN%"=="mvn" (
    call mvn %MVN_ARGS%
) else (
    call "%MVN%" %MVN_ARGS%
)
if errorlevel 1 (
    set "FAILMSG=Maven build failed. Scroll up for the compiler errors (add --skip-tests to skip tests)."
    goto fail
)

rem ------------------------------------------------------------
rem 5. check the artifacts
rem ------------------------------------------------------------
echo.
echo [2/3] checking build output ...
set "JARNAME="
for %%f in ("target\OceanNeedle-java-*.jar") do set "JARNAME=%%~nxf"
if not defined JARNAME (
    set "FAILMSG=target\OceanNeedle-java-*.jar was not produced"
    goto fail
)
if not exist "target\lib" (
    set "FAILMSG=target\lib was not produced - dependency copy failed"
    goto fail
)
set "LIBCNT=0"
for %%f in ("target\lib\*.jar") do set /a LIBCNT+=1
set "JARSIZE=0"
for %%f in ("target\%JARNAME%") do set "JARSIZE=%%~zf"
set /a JARMB=%JARSIZE%/1048576
echo     jar         : target\%JARNAME%
echo     dependencies: %LIBCNT% jars
echo.

if "%MAKE_DIST%"=="0" goto after_dist

rem ------------------------------------------------------------
rem 6. assemble the run folder
rem    description\*.txt and user.json are read through RELATIVE
rem    paths (src\main\resources\...) at runtime and are NOT inside
rem    the jar, so the same folder layout must be recreated in dist
rem    and run.bat has to cd into it first.
rem ------------------------------------------------------------
if not exist "run.bat" (
    set "FAILMSG=run.bat is missing from the project root, cannot assemble dist"
    goto fail
)
echo [3/3] assembling dist\OceanNeedle-win64 ...
set "DISTDIR=%ROOT%\dist\OceanNeedle-win64"
if exist "%DISTDIR%" rmdir /s /q "%DISTDIR%"
mkdir "%DISTDIR%"
copy /y "target\%JARNAME%" "%DISTDIR%\" >nul
xcopy "target\lib" "%DISTDIR%\lib\" /E /I /Y /Q >nul
xcopy "src\main\resources\description" "%DISTDIR%\src\main\resources\description\" /E /I /Y /Q >nul
if exist "src\main\resources\user.json" copy /y "src\main\resources\user.json" "%DISTDIR%\src\main\resources\" >nul
if exist "src\main\resources\jdbc.properties" copy /y "src\main\resources\jdbc.properties" "%DISTDIR%\jdbc.properties" >nul
copy /y "run.bat" "%DISTDIR%\run.bat" >nul
if exist "README.md" copy /y "README.md" "%DISTDIR%\README.md" >nul
echo     done
echo.

:after_dist

if "%MAKE_ZIP%"=="0" goto after_zip
if "%MAKE_DIST%"=="0" goto after_zip
if not exist "%ROOT%\dist" goto after_zip
echo [zip] creating archive ...
pushd "%ROOT%\dist"
powershell -NoProfile -ExecutionPolicy Bypass -Command "Compress-Archive -Path 'OceanNeedle-win64' -DestinationPath 'OceanNeedle-win64.zip' -Force" >nul 2>nul
if errorlevel 1 (
    echo       zip failed - you can right-click compress dist\OceanNeedle-win64 manually
) else (
    echo       done: dist\OceanNeedle-win64.zip
)
popd
echo.

:after_zip

rem ------------------------------------------------------------
rem 7. summary
rem ------------------------------------------------------------
echo ============================================================
echo   Build finished
echo ============================================================
echo   jar        : target\%JARNAME%  (~%JARMB% MB)
echo   deps       : target\lib  (%LIBCNT% jars)
if "%MAKE_DIST%"=="1" echo   run folder : dist\OceanNeedle-win64
if "%MAKE_ZIP%"=="1" if exist "%ROOT%\dist\OceanNeedle-win64.zip" echo   zip        : dist\OceanNeedle-win64.zip
echo.
echo   To run     : open dist\OceanNeedle-win64 and double-click run.bat
echo                (run.bat also works from the project root)
echo.
echo   WARNING: src\main\resources\jdbc.properties is packaged into the
echo            jar, so the plain-text database password travels with
echo            the jar and with dist. Do not share them with anyone.
echo.
goto done_ok

rem ============================================================
rem subroutines
rem ============================================================

:detect_maven
if defined MAVEN_HOME if exist "%MAVEN_HOME%\bin\mvn.cmd" (
    set "MVN=%MAVEN_HOME%\bin\mvn.cmd"
    exit /b 0
)
if defined M2_HOME if exist "%M2_HOME%\bin\mvn.cmd" (
    set "MVN=%M2_HOME%\bin\mvn.cmd"
    exit /b 0
)
where mvn >nul 2>nul
if not errorlevel 1 (
    set "MVN=mvn"
    exit /b 0
)
rem fallback: Maven bundled with IntelliJ IDEA (common when mvn is not on PATH)
for /d %%d in ("%LOCALAPPDATA%\Programs\IntelliJ IDEA*") do if not defined MVN if exist "%%d\plugins\maven\lib\maven3\bin\mvn.cmd" set "MVN=%%d\plugins\maven\lib\maven3\bin\mvn.cmd"
for /d %%d in ("%ProgramFiles%\JetBrains\IntelliJ IDEA*") do if not defined MVN if exist "%%d\plugins\maven\lib\maven3\bin\mvn.cmd" set "MVN=%%d\plugins\maven\lib\maven3\bin\mvn.cmd"
if not defined MVN exit /b 1
exit /b 0

:make_jdbc
if not exist "src\main\resources\jdbc.properties.example" (
    set "FAILMSG=src\main\resources\jdbc.properties is missing"
    goto fail
)
copy /y "src\main\resources\jdbc.properties.example" "src\main\resources\jdbc.properties" >nul
echo [NOTE] created src\main\resources\jdbc.properties from the template.
echo        Fill in the real host / user / password, then run build.bat again.
echo.
set "FAILMSG=jdbc.properties was just created - edit it and run again"
goto fail

:usage
echo Usage: build.bat [options]
echo.
echo   --skip-tests   skip the unit tests (faster)
echo   --no-dist      only build target\ jar + lib, do not assemble dist
echo   --no-zip       do not create the zip archive
echo   --no-pause     do not wait for a key press (for scripting)
echo   -h, --help     show this help
echo.
goto done_ok

:done_ok
if "%NO_PAUSE%"=="1" exit /b 0
echo Press any key to close this window ...
pause >nul
exit /b 0

:fail
echo.
echo [ERROR] %FAILMSG%
echo.
if "%NO_PAUSE%"=="1" exit /b 1
echo Press any key to close this window ...
pause >nul
exit /b 1
