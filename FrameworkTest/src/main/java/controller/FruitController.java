package com.controller;

import com.annotation.AnnotationController;
import com.annotation.UrlMapping;

@AnnotationController
public class FruitController {

    @UrlMapping("/fruit/list")
    public void lister() {
    }
}
