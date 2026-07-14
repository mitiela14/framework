#!/bin/bash
# Sprint 0 - Compile le framework , cree framework.jar, 
# le copie dans le FrameworkTest/lib 
SERVLET_API_JAR="../FrameworkTest/lib/servlet-api.jar"
TARGET_LIB_DIR="../FrameworkTest/lib"
JAR_NAME="framework.jar"

echo "Nettoyage"
rm -rf bin
mkdir -p bin

echo "Compilation"
find src/main/java -name "*.java" > sources.txt
javac -encoding UTF-8 -d bin -cp "$SERVLET_API_JAR" @sources.txt
if [ $? -ne 0 ]; then
    echo "Erreur de compilation"
    rm -f sources.txt
    exit 1
fi
rm -f sources.txt

echo "Creation du jar"
jar -cf "$JAR_NAME" -C bin .

echo "Copie vers le projet du test"
mkdir -p "$TARGET_LIB_DIR"
cp "$JAR_NAME" "$TARGET_LIB_DIR/"

echo "Termine : $TARGET_LIB_DIR/$JAR_NAME"