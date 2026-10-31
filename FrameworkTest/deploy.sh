#!/bin/bash

SERVLET_API_JAR="../FrameworkTest/lib/servlet-api.jar"
TARGET_LIB_DIR="../FrameworkTest/lib"
JAR_NAME="framework.jar"

SOURCES="sources.txt"

echo "[1/4] Nettoyage local..."
rm -rf bin
mkdir -p bin

echo "[2/4] Compilation globale du Framework..."
# Generation de la liste de tous les fichiers .java (recursif)
find src/main/java -name "*.java" > "$SOURCES"

javac -encoding UTF-8 -parameters -d bin -cp "$SERVLET_API_JAR:lib/framework.jar" @"$SOURCES"
if [ $? -ne 0 ]; then
    echo
    echo "[ERRpackage com.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.annotation.*;
import com.model.ModelView;

@AnnotationController
public class ApiController {

    @JsonResponse
    @UrlMapping(value = "/api/fruits", method = "GET")
    public List<String> listeFruitsJson() {
        List<String> fruits = new ArrayList<>();
        fruits.add("Pomme");
        fruits.add("Banane");
        fruits.add("Orange");
        return fruits;
    }

    @JsonResponse
    @UrlMapping(value = "/api/emp", method = "GET")
    public Map<String, Object> empJson() {
        Map<String, Object> emp = new HashMap<>();
        emp.put("nom", "Rakoto");
        emp.put("prenom", "Jean");
        emp.put("age", 25);
        emp.put("actif", true);
        return emp;
    }

    @JsonResponse
    @UrlMapping(value = "/api/objets", method = "GET")
    public List<Object> listeObjetsJson() {
        List<Object> objets = new ArrayList<>();
        Map<String, Object> a = new HashMap<>();
        a.put("id", 1);
        a.put("nom", "Produit A");
        Map<String, Object> b = new HashMap<>();
        b.put("id", 2);
        b.put("nom", "Produit B");
        objets.add(a);
        objets.add(b);
        return objets;
    }

    // Sans @JsonResponse : comportement MVC classique (vue)
    @UrlMapping(value = "/api/vue", method = "GET")
    public ModelView vueNormale() {
        ModelView mv = new ModelView("ListeFruits");
        List<String> fruits = new ArrayList<>();
        fruits.add("Pomme");
        mv.getData().put("Fruits", fruits);
        return mv;
    }

}EUR] La compilation a echoue !"
    rm -f "$SOURCES"
    exit 1
fi
# Suppression du fichier temporaire si tout s'est bien passe
rm -f "$SOURCES"
echo "[OK] Compilation reussie de toutes les classes."

echo "[3/4] Creation du JAR..."
jar -cf "$JAR_NAME" -C bin .
if [ $? -ne 0 ]; then
    echo "[ERREUR] Impossible de creer le fichier JAR."
    exit 1
fi
echo "[OK] Fichier $JAR_NAME cree avec succes."

echo "[4/4] Deploiement vers le projet de test..."
# Verification si le dossier de destination existe, sinon on le cree
mkdir -p "$TARGET_LIB_DIR"

# Suppression de l'ancien JAR s'il existe
if [ -f "$TARGET_LIB_DIR/$JAR_NAME" ]; then
    echo "Suppression de l'ancien $JAR_NAME dans le dossier de test..."
    rm -f "$TARGET_LIB_DIR/$JAR_NAME"
fi

# Copie du nouveau JAR
cp "$JAR_NAME" "$TARGET_LIB_DIR/"

if [ $? -ne 0 ]; then
    echo "[ERREUR] Impossible de copier le JAR vers le dossier de test."
    exit 1
fi

echo
echo "============================================"
echo "   Termine ! Framework deployee dans :"
echo "   $TARGET_LIB_DIR/$JAR_NAME"
echo "============================================"