package com.controller;

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

}