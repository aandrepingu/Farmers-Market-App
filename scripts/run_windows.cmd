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

javac .\src\gui\java\*.java .\src\model\java\*.java -d bin --module-path %javafxpath% --add-modules javafx.controls,javafx.fxml -cp %mysqlpath%
java --module-path %javafxpath% --add-modules javafx.controls,javafx.fxml -cp "bin;%mysqlpath%" gui.java.FarmersMarketGUI