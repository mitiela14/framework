package com.controller;

import com.annotation.AnnotationController;
import com.annotation.UrlMapping;

@AnnotationController
public class FruitController {

    @UrlMapping("/fruit/list")
    public void lister() {
        System.out.println("FruitController.lister() est appellee");
    }

    @UrlMapping(value="/fruit/ajouter", method="POST")
    public void sauvegarder() {
        System.out.println("FruitController.sauvegarder() est appellee");
    }
}
