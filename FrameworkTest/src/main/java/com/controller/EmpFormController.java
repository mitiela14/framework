package com.controller;

import com.annotation.AnnotationController;
import com.annotation.JsonResponse;
import com.annotation.UrlMapping;
import com.entity.Employe;
import com.model.MapFile;
import com.model.ModelView;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@AnnotationController
public class EmpFormController {


    @UrlMapping(value = "/emp/nouveau", method = "GET")
    public ModelView formulaire() {
        ModelView mv = new ModelView("FormEmp");
        mv.addAttribute("titre", "Ajouter un employe");
        return mv;
    }

    @UrlMapping(value = "/emp/enregistrer", method = "POST")
    public ModelView save(String nom, int age) {
        System.out.println("[CONTROLEUR] save()appele avec nom=" + nom
                + " age=" + age);

        ModelView mv = new ModelView("EmpEnregistre");
        mv.addAttribute("nom", nom);
        mv.addAttribute("age", age);
        return mv;
    }

    @JsonResponse
    @UrlMapping(value = "/api/emp/enregistrer", method = "POST")
    public Map<String, Object> saveJson(String nom, int age) {
        Map<String, Object> reponse = new LinkedHashMap<>();
        reponse.put("nom", nom);
        reponse.put("age", age);
        reponse.put("message", "Employe enregistre par le binding");
        return reponse;
    }

    @UrlMapping(value = "/emp/enregistrer-vide", method = "POST")
    public ModelView saveSansValeur(String nom, Integer anneeNaissance, double salaire) {
        ModelView mv = new ModelView("EmpEnregistre");
        mv.addAttribute("nom", nom);
        mv.addAttribute("age", anneeNaissance);   // Integer absent -> null
        mv.addAttribute("actif", (salaire > 0));
        return mv;
    }

    @UrlMapping(value = "/emp/enregistrer-objet", method = "POST")
    public ModelView save(Employe employe) {
        System.out.println("[CONTROLEUR] save(Employe) appele avec " + employe);

        ModelView mv = new ModelView("EmpObjet");
        mv.addAttribute("employe", employe);
        return mv;
    }

    @JsonResponse
    @UrlMapping(value = "/api/emp/enregistrer-objet", method = "POST")
    public Employe saveObjetJson(Employe employe) {
        System.out.println("[CONTROLEUR] saveObjetJson() appele avec " + employe);
        return employe;
    }

    public String save(String nom) {
        return "Surcharge a 1 parametre : nom=" + nom;
    }

    @UrlMapping(value = "/emp/nouveau-scan", method = "GET")
    public ModelView formulaireScan() {
        ModelView mv = new ModelView("FormScan");
        mv.addAttribute("titre", "Ajouter un employe avec le scan de la CIN");
        return mv;
    }

    @UrlMapping(value = "/emp/enregistrer-scan", method = "POST")
    public ModelView saveScan(String nom, MapFile rectoCIN, byte[] scanCIN, ServletContext ctx) {
        System.out.println("[CONTROLEUR] saveScan() nom=" + nom
                + " rectoCIN=" + (rectoCIN == null ? "aucun" : rectoCIN)
                + " scanCIN=" + (scanCIN == null ? "aucun" : scanCIN.length + " octets"));

        ModelView mv = new ModelView("EmpScan");
        mv.addAttribute("nom", nom);
        mv.addAttribute("rectoCIN", rectoCIN);
        mv.addAttribute("scanCIN", scanCIN);
        sauvegarder(rectoCIN, "recto-" + nom, ctx, mv);
        return mv;
    }


    @UrlMapping(value = "/emp/enregistrer-scan-objet", method = "POST")
    public ModelView saveScanObjet(Employe employe, ServletContext ctx) {
        System.out.println("[CONTROLEUR] saveScanObjet() " + employe);

        ModelView mv = new ModelView("EmpScan");
        mv.addAttribute("employe", employe);
        mv.addAttribute("nom", employe.getNom());
        mv.addAttribute("rectoCIN", employe.getCinRecto());
        mv.addAttribute("scanCIN", employe.getScanCIN());
        sauvegarder(employe.getCinRecto(), "cin-" + employe.getNom(), ctx, mv);
        return mv;
    }


    @JsonResponse
    @UrlMapping(value = "/api/emp/enregistrer-scan", method = "POST")
    public Map<String, Object> saveScanJson(String nom, MapFile rectoCIN, byte[] scanCIN) {
        Map<String, Object> reponse = new LinkedHashMap<>();
        reponse.put("nom", nom);
        reponse.put("fichier", rectoCIN);
        reponse.put("scanCIN", scanCIN);
        reponse.put("message", "fichier(s) recus par le binding multipart");
        return reponse;
    }


    private void sauvegarder(MapFile fichier, String nomVoulu, ServletContext ctx, ModelView mv) {
        if (fichier == null || fichier.estVide()) {
            mv.addAttribute("stockage", "aucun fichier a enregistrer");
            return;
        }
        try {
            String dossier = (ctx == null) ? null : ctx.getRealPath("/uploads");
            String extension = fichier.getExtension();
            String nomSurDisque = nomVoulu + (extension.isEmpty() ? "" : "." + extension);
            mv.addAttribute("stockage", fichier.ecrire(dossier, nomSurDisque));
        } catch (IOException e) {
            mv.addAttribute("stockage", "ECHEC de sauvegarde : " + e.getMessage());
        }
    }
}