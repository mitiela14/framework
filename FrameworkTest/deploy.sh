#!/bin/bash
#Sprint 0 - Cree FrameworkTest.war et lecopie dans Tomcat
#Usage: TOMCAT WEBAPPS=/chemin/vers/tomcat/webapps .deploy.sh
APP_NAME="FrameworkTest"
WEB_DIR="src/main/webapp"
BUILD_DIR="build"
LIB_DIR="lib"
TOMCAT_WEBAPPS="${TOMCAT_WEBAPPS:-/home/elmitia/Documents/apache-tomcat-8.5.75/webapps}"

echo "Nettoyage"
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR/WEB-INF/classes" "$BUILD_DIR/WEB-INF/lib"

cp -r "$WEB_DIR"/* "$BUILD_DIR"/
cp "$LIB_DIR/framework.jar" "$BUILD_DIR/WEB-INF/lib/"

(cd "$BUILD_DIR" && jar -cvf "$APP_NAME.war" *)
cp "$BUILD_DIR/$APP_NAME.war" "$TOMCAT_WEBAPPS/"
echo "Deploiement termine ! Redemarrez Tomcat si besoin."
