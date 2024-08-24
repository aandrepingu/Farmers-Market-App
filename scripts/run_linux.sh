#!/usr/bin/bash

if [ $# -ne 2 ]; then
    echo "Usage: $0 <path-to-javafx-jar-folder> <path-to-mysql-connector-jar>"
    exit 1
fi

javafxpath=$1
mysqlpath=$2

if [ -d "$javafxpath" ] && [ -f "$mysqlpath" ]; then
    javac ./src/gui/java/*.java ./src/model/java/*.java -d bin --module-path "$javafxpath" --add-modules javafx.controls,javafx.fxml -cp "$mysqlpath"

    java --module-path "$javafxpath" --add-modules javafx.controls,javafx.fxml -cp "bin:$mysqlpath" gui.java.FarmersMarketGUI
else
    echo "Invalid paths provided."
    exit 1
fi
