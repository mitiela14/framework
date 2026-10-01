package com.controller;

import com.annotation.AnnotationController;
import com.annotation.UrlMapping;

@AnnotationController
public class EmpController {

    // /emp/list  ->  EmpController.liste()
    @UrlMapping("/emp/list")
    public void liste() {
        // Le corps sera execute au Sprint 3bis (invoke)
    }

    // /emp/new  ->  EmpController.create()
    @UrlMapping("/emp/new")
    public void create() {
    }

    // Pas d'annotation : cette methode n'a PAS d'URL
    public void methodeInterne() {
    }
}
