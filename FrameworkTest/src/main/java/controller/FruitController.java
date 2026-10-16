package com.controller;

import java.util.ArrayList;
import java.util.List;

import com.annotation.AnnotationController;
import com.annotation.UrlMapping;
import com.model.ModelView;

@AnnotationController
public class FruitController {

    @UrlMapping("/les-fruits")
    public ModelView afficherLesFruits() {
        List<String> fruits = new ArrayList<>();
        fruits.add("Pomme");
        fruits.add("Banane");
        fruits.add("Orange");

        ModelView mv = new ModelView("ListeFruits");
        mv.addAttribute("titre", "Fruits de la saison");
        mv.addAttribute("fruits", fruits);
        mv.addAttribute("nombre", fruits.size());
        return mv;
    }



    @UrlMapping("/fruit/list")
    public void lister() {
        System.out.println("FruitController.lister() est appellee");
    }

    @UrlMapping(value="/fruit/ajouter", method="POST")
    public void sauvegarder() {
        System.out.println("FruitController.sauvegarder() est appellee");
    }
}
