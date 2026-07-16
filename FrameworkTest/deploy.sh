#!/bin/bash

#Sprint 0 - Cree FrameworkTest.war et lecopie dans Tomcat
#Usage: TOMCAT WEBAPPS=/chemin/vers/tomcat/webapps .deploy.sh


# Sprint 1 - Compile les controleurs, cree FrameworkTest.war et le copie dans Tomcat
# Usage : TOMCAT_WEBAPPS=/chemin/vers/tomcat/webapps ./deploy.sh
APP_NAME="FrameworkTest"
SRC_DIR="src/main/java"
WEB_DIR="src/main/webapp"
BUILD_DIR="build"
LIB_DIR="lib"
TOMCAT_WEBAPPS="${TOMCAT_WEBAPPS:-/home/elmitia/Documents/apache-tomcat-8.5.75/webapps}"

if [ ! -f "$LIB_DIR/framework.jar" ]; then
    echo "[ERREUR] lib/framework.jar introuvable : lancez d'abord Framework/build.sh"
    exit 1
fi

rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR/WEB-INF/classes" "$BUILD_DIR/WEB-INF/lib"

# NOUVEAU (Sprint 1) : compilation des controleurs.
# Ils utilisent @AnnotationController => framework.jar doit etre dans le classpath.
find "$SRC_DIR" -name "*.java" > sources.txt
javac -encoding UTF-8 -cp "$LIB_DIR/servlet-api.jar:$LIB_DIR/framework.jar" \
      -d "$BUILD_DIR/WEB-INF/classes" @sources.txt
if [ $? -ne 0 ]; then
    echo "[ERREUR] La compilation du projet de test a echoue !"
    rm -f sources.txt
    exit 1
fi
rm -f sources.txt

cp -r "$WEB_DIR"/* "$BUILD_DIR"/
cp "$LIB_DIR/framework.jar" "$BUILD_DIR/WEB-INF/lib/"

(cd "$BUILD_DIR" && jar -cvf "$APP_NAME.war" *)
cp "$BUILD_DIR/$APP_NAME.war" "$TOMCAT_WEBAPPS/"
echo "Deploiement termine ! Redemarrez Tomcat si besoin."
