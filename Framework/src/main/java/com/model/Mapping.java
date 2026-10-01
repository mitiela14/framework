package com.model;

/**
 * SPRINT 2 - Le "resultat" d'une URL : quel controleur, quelle methode.
 * Ex : /emp/list  ->  Mapping("com.controller.EmpController", "liste")
 */
public class Mapping {
    private String className;   // nom complet de la classe controleur
    private String method;      // nom de la methode

    public Mapping(String className, String method) {
        this.className = className;
        this.method = method;
    }

    public String getClassName() { return className; }
    public String getMethod()    { return method; }

    @Override
    public String toString() {
        return className + "." + method + "()";
    }
}
