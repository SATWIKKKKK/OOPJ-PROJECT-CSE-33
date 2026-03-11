@echo off
:: Always run from the directory where this script lives (fixes double-click from Explorer)
cd /d "%~dp0"

echo ========================================
echo  College Admission and Fee System
echo  PostgreSQL + Java OOP
echo ========================================

if not exist out mkdir out
if not exist lib (
    echo [ERROR] lib/ folder not found!
    echo Please download postgresql-42.x.x.jar from:
    echo   https://jdbc.postgresql.org/download/
    echo and place it in the lib/ folder.
    pause & exit /b 1
)

set JDBC_JAR=
for %%f in (lib\postgresql-*.jar) do set JDBC_JAR=%%f

if "%JDBC_JAR%"=="" (
    echo [ERROR] No postgresql-*.jar found in lib/
    pause & exit /b 1
)

echo Using driver: %JDBC_JAR%
echo Compiling...

javac -cp "%JDBC_JAR%" -d out ^
  src\com\college\models\*.java ^
  src\com\college\interfaces\*.java ^
  src\com\college\exceptions\*.java ^
  src\com\college\database\*.java ^
  src\com\college\services\*.java ^
  src\com\college\Main.java

if %ERRORLEVEL% NEQ 0 ( echo [ERROR] Compilation failed. & pause & exit /b 1 )

echo [OK] Compiled! Running...
echo ========================================
java -cp "out;%JDBC_JAR%" com.college.Main
pause
