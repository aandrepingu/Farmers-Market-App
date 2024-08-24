@echo off
if "%~1"=="" (
    echo "Usage: run_windows.cmd <path-to-javafx-jar-folder> <path-to-mysql-connector-jar>"
    exit /b 1
)

if "%~2"=="" (
    echo "Usage: run_windows.cmd <path-to-javafx-jar-folder> <path-to-mysql-connector-jar>"
    exit /b 1
)
set "javafxpath=%~1"
set "mysqlpath=%~2"


javadoc -private .\src\gui\java\*.java .\src\model\java\*.java -d .\docs --module-path %javafxpath% --add-modules javafx.controls,javafx.fxml -cp %javafxpath%
