package com.controller.admin;

import com.annotation.AnnotationController;
import com.annotation.Authenticated;
import com.annotation.Authorized;
import com.annotation.UrlMapping;

// Dans un SOUS-package : pour verifier que le scan est recursif
@AnnotationController
public class AdminController {

    // "sans slash" : le framework le normalise en /admin/home
    @Authenticated
    @Authorized(roles = {"ADMIN"})
    @UrlMapping("admin/home")
    public void accueil() {
        System.out.println("AdminController.accueil() est appelee");
    }
}
