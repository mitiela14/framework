package com.controller;

import java.util.ArrayList;
import java.util.List;

import com.annotation.*;
import com.model.ModelView; 

@AnnotationController
public class FruitController {

    @UrlMapping(value = "/les-fruits", method = "GET")
    public ModelView afficherLesFruits() {
        List<String> fruits = new ArrayList<>();
        fruits.add("Pomme");
        fruits.add("Banane");
        fruits.add("Orange");

        ModelView mv = new ModelView("ListeFruits");
        
        mv.getData().put("Fruits", fruits);

        return mv;
    }
}