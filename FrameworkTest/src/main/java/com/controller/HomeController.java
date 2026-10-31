package com.controller;

import com.annotation.AnnotationController;
import com.annotation.UrlMapping;
import com.model.ModelView;

@AnnotationController
public class HomeController {

    @UrlMapping("/")
    public ModelView accueil() {
        ModelView mv = new ModelView("Accueil");                 // nom de la vue : /WEB-INF/Views/Accueil.jsp
        mv.addAttribute("titre", "Bienvenue sur le framework");
        mv.addAttribute("message", "Cette page est affichee par une JSP, via un ModelView.");
        return mv;
    }

    @UrlMapping("/vue-inconnue")
    public ModelView vueInconnue() {
        return new ModelView("Nexiste");
    }
}
