@echo off
cd /d "%~dp0"

set JAVA="C:\Program Files\Java\jdk-25\bin\java.exe"
set FX="openjfx-25.0.1_windows-x64_bin-sdk\javafx-sdk-25.0.1\lib"

%JAVA% --module-path %FX% ^
       --add-modules javafx.controls,javafx.graphics,javafx.fxml ^
       -jar app.jar

echo.
echo ==========================
echo  Press any key to exit...
echo ==========================
echo dev mode
pause >nul
