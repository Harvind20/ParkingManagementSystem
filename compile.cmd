@echo off
echo Cleaning old class files and database...
del /S /Q src\*.class
del parking_FINAL.db
echo Compiling...

javac -cp "lib/sqlite-jdbc-3.51.2.0.jar" src/EntryModule/*.java src/coreParkingSystem/*.java src/UserInterface/*.java src/ExitModule/*.java src/FineModule/*.java
if %errorlevel% neq 0 (
    echo [ERROR] Compilation Failed!
    pause
    exit /b %errorlevel%
)

echo.
echo ========================================
echo   Launching Parking System Main Menu
echo ========================================
echo.

java -cp "src;lib/sqlite-jdbc-3.51.2.0.jar" UserInterface.MainMenuUI

pause