#!/usr/bin/bash

if [ $# -ne 2 ]; then
    echo "Usage: $0 <path-to-javafx-jar-folder> <path-to-mysql-connector-jar>"
    exit 1
fi

javafxpath=$1
mysqlpath=$2

if [ -d "$javafxpath" ] && [ -f "$mysqlpath" ]; then
    javadoc -private -d docs src/model/java/*.java src/gui/java/*.java --module-path "$javafxpath" --add-modules javafx.controls,javafx.fxml
fi


