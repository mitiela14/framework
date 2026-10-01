#!/bin/bash
# Compile et lance l'exercice (hors web, juste un main)
rm -rf out && mkdir out
javac -encoding UTF-8 -d out exercice/*.java && java -cp out exercice.Main
