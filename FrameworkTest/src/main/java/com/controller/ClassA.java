package com.controller;

import com.annotation.UrlMapping;

// PAS de @AnnotationController : la classe n'est pas un controleur,
// donc l'URL ci-dessous doit etre IGNOREE (=> 404)
public class ClassA {

    @UrlMapping("/classa/test")
    public void test() {
    }
}
