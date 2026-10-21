package com.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.TYPE)            // utilisable seulement sur une classe
@Retention(RetentionPolicy.RUNTIME)  // gardee jusqu'a l'execution pour que la reflexion la voie
public @interface AnnotationController {

}