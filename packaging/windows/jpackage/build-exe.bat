@echo off
setlocal enabledelayedexpansion
cd /d "%~dp0"

rem ----------------------------------------------
rem 9pazzle EXE builder (jpackage)
rem ----------------------------------------------

set "APP_NAME=9pazzle"
set "APP_VERSION=1.0.0"
set "MAIN_JAR=app.jar"
set "MAIN_CLASS=tetris.Main"
set "WIN_MENU_GROUP=9pazzle"
set "VENDOR=9pazzle Team"

set "PROJECT_ROOT=%~dp0\..\..\.."
set "INPUT_DIR=%~dp0input"
set "DIST_DIR=%~dp0dist"
set "CONFIG_FILE=%~dp0config\launcher.properties"
set "ICON_FILE=%~dp0assets\app.ico"

set "FX_JMODS=%PROJECT_ROOT%\openjfx-25.0.1_windows-x64_bin-sdk\javafx-jmods-25.0.1"

if not exist "%PROJECT_ROOT%\%MAIN_JAR%" (
  echo [ERROR] %MAIN_JAR% not found at project root: %PROJECT_ROOT%
  exit /b 1
)

if not exist "%FX_JMODS%" (
  echo [ERROR] JavaFX JMODS not found.
  echo         Place JavaFX jmods at:
  echo         %FX_JMODS%
  echo         (example: javafx.controls.jmod, javafx.graphics.jmod ...)
  exit /b 1
)

where jpackage >nul 2>nul
if errorlevel 1 (
  echo [ERROR] jpackage not found. Use JDK 17+.
  exit /b 1
)

if exist "%INPUT_DIR%" rmdir /s /q "%INPUT_DIR%"
mkdir "%INPUT_DIR%"

copy /y "%PROJECT_ROOT%\%MAIN_JAR%" "%INPUT_DIR%\%MAIN_JAR%" >nul

if exist "%PROJECT_ROOT%\images" xcopy "%PROJECT_ROOT%\images" "%INPUT_DIR%\images" /e /i /y >nul
if exist "%PROJECT_ROOT%\audio" xcopy "%PROJECT_ROOT%\audio" "%INPUT_DIR%\audio" /e /i /y >nul

if exist "%DIST_DIR%" rmdir /s /q "%DIST_DIR%"
mkdir "%DIST_DIR%"

set "ICON_OPT="
if exist "%ICON_FILE%" set "ICON_OPT=--icon \"%ICON_FILE%\""

jpackage ^
  --type exe ^
  --dest "%DIST_DIR%" ^
  --name "%APP_NAME%" ^
  --app-version "%APP_VERSION%" ^
  --vendor "%VENDOR%" ^
  --input "%INPUT_DIR%" ^
  --main-jar "%MAIN_JAR%" ^
  --main-class "%MAIN_CLASS%" ^
  --module-path "%FX_JMODS%" ^
  --add-modules javafx.controls,javafx.graphics,javafx.fxml,javafx.media ^
  --java-options "-Dfile.encoding=UTF-8" ^
  --java-options "-Dprism.order=d3d,sw" ^
  --java-options "-Djavafx.preloader=none" ^
  --resource-dir "%~dp0assets" ^
  --win-shortcut ^
  --win-menu ^
  --win-menu-group "%WIN_MENU_GROUP%" ^
  --license-file "%~dp0LICENSE.txt" ^
  --add-launcher "%APP_NAME%_launcher=%CONFIG_FILE%" %ICON_OPT%

if errorlevel 1 (
  echo [ERROR] jpackage failed.
  exit /b 1
)

echo.
echo [OK] EXE generated in: %DIST_DIR%
exit /b 0
