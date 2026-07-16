package com.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/** SPRINT 2 - Associe une URL a une METHODE d'un controleur.
 * Exemple :  @UrlMapping("/emp/list")  public void liste() { ... }
 */
@Target(ElementType.METHOD)          // seulement sur une methode
@Retention(RetentionPolicy.RUNTIME)  // visible par la reflexion
public @interface UrlMapping {
    String value();
}

