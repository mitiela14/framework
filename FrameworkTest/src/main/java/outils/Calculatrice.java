package com.outils;

import com.annotation.AnnotationController;
import com.annotation.UrlMapping;

// Annotee mais HORS du package scanne (com.controller) : doit etre IGNOREE
@AnnotationController
public class Calculatrice {

    @UrlMapping("/calcul/somme")
    public void somme() {
    }
}
