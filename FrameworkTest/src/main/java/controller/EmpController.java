package com.controller;

import com.annotation.AnnotationController;
import com.annotation.UrlMapping;

@AnnotationController
public class EmpController {

    // /emp/list  ->  EmpController.liste()
    @UrlMapping("/emp/list")
    public String liste() {
        System.out.println("EmpController.liste() est appellee");
        return "Liste des employes : Nirisoa, Valisoa, Naina";
    }

    // /emp/new  ->  EmpController.create()
    @UrlMapping("/emp/new")
    public void create() {
        System.out.println("EmpController.create() est appellee");
    }

    @UrlMapping(value="/emp/new", method="POST")
    public void enregistrer() {
        System.out.println("EmpController.enregistrer() est appellee");
    }

    @UrlMapping("/emp/erreur")
    public void erreur() {
        System.out.println("EmpController.erreur() est appellee");
        throw new IllegalArgumentException("Erreur volontaire dans le controlleur");
    }
}
