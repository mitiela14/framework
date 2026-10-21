#!/bin/bash

set -e

APP_NAME="FrameworkTest"
SRC_DIR="src/main/java"
WEB_DIR="src/main/webapp"
BUILD_DIR="build"
LIB_DIR="lib"
TOMCAT_WEBAPPS="${TOMCAT_WEBAPPS:-/home/elmitia/Documents/apache-tomcat-8.5.75/webapps}"
SERVLET_API_JAR="$LIB_DIR/servlet-api.jar"

echo "[1/4] Nettoyage et preparation de $BUILD_DIR ..."
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR/WEB-INF/classes" "$BUILD_DIR/WEB-INF/lib"

echo "[2/4] Compilation des controleurs du projet de test ..."
find "$SRC_DIR" -name "*.java" > sources.txt
# -parameters : conserve le NOM des parametres des methodes (necessaire au binding du Sprint 7)
javac -encoding UTF-8 -parameters \
      -cp "$SERVLET_API_JAR:$LIB_DIR/*" \
      -d "$BUILD_DIR/WEB-INF/classes" @sources.txt
rm -f sources.txt
echo "[OK] Compilation reussie."

echo "[3/4] Copie des fichiers web (web.xml, JSP) et du framework.jar ..."
cp -r "$WEB_DIR"/* "$BUILD_DIR"/
# On copie UNIQUEMENT framework.jar : servlet-api.jar est fourni par Tomcat
cp "$LIB_DIR/framework.jar" "$BUILD_DIR/WEB-INF/lib/"

echo "[4/4] Creation du WAR et deploiement dans Tomcat ..."
(cd "$BUILD_DIR" && jar -cvf "$APP_NAME.war" * > /dev/null)
if [ -d "$TOMCAT_WEBAPPS" ]; then
    cp "$BUILD_DIR/$APP_NAME.war" "$TOMCAT_WEBAPPS"/
    echo "WAR copie dans $TOMCAT_WEBAPPS"
else
    echo "ATTENTION : $TOMCAT_WEBAPPS introuvable."
    echo "Le WAR est prêt ici : $BUILD_DIR/$APP_NAME.war"
fi

echo "============================================"
echo "   Termine ! Redemarrez Tomcat si besoin."
echo "============================================"