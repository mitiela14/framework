package com.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;


public class MySession {

    private final HttpServletRequest requete;

    private final Map<String, Object> valeurs = new LinkedHashMap<>();

    private final Set<String> retirees = new LinkedHashSet<>();

    private final boolean nouvelle;

    private boolean modifiee = false;

    private boolean invalidee = false;


    MySession(HttpServletRequest requete) {
        this.requete = requete;
        HttpSession http = requete.getSession(false);
        this.nouvelle = (http == null);
        if (http != null) {
            for (String nom : Collections.list(http.getAttributeNames())) {
                valeurs.put(nom, http.getAttribute(nom));
            }
        }
    }

    public Object get(String nom) {
        return valeurs.get(nom);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String nom, Class<T> type) {
        Object valeur = valeurs.get(nom);
        if (valeur == null) return null;
        if (!type.isInstance(valeur)) {
            throw new ClassCastException("l'attribut de session '" + nom
                    + "' est de type " + valeur.getClass().getName()
                    + " et non de type " + type.getName());
        }
        return (T) valeur;
    }

    public Object get(String nom, Object parDefaut) {
        Object valeur = valeurs.get(nom);
        return (valeur == null) ? parDefaut : valeur;
    }

    public MySession set(String nom, Object valeur) {
        verifierNom(nom);
        valeurs.put(nom, valeur);
        retirees.remove(nom);
        modifiee = true;
        return this;
    }

    public Object remove(String nom) {
        Object ancienne = valeurs.remove(nom);
        if (ancienne != null) {
            retirees.add(nom);
            modifiee = true;
        }
        return ancienne;
    }

    public boolean contient(String nom) {
        return valeurs.containsKey(nom);
    }

    public int taille() {
        return valeurs.size();
    }

    public boolean estVide() {
        return valeurs.isEmpty();
    }

    public Set<String> noms() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(valeurs.keySet()));
    }

    public Map<String, Object> toutes() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(valeurs));
    }

    public void vider() {
        for (String nom : valeurs.keySet()) {
            retirees.add(nom);
        }
        valeurs.clear();
        modifiee = true;
    }


    public void invalider() {
        valeurs.clear();
        retirees.clear();
        modifiee = true;
        invalidee = true;
        HttpSession http = requete.getSession(false);
        if (http != null) {
            http.invalidate();
        }
    }

    /** L'identifiant de la session servlet, ou null s'il n'y en a pas encore. */
    public String getId() {
        HttpSession http = requete.getSession(false);
        return (http == null) ? null : http.getId();
    }


    public boolean estNouvelle() {
        return nouvelle;
    }

    public boolean estInvalidee() {
        return invalidee;
    }

    @Override
    public String toString() {
        return "MySession(id=" + getId() + ", " + valeurs.size() + " attribut(s)"
                + (invalidee ? ", invalidee" : "") + ")";
    }


    void ecrireVersHttpSession() {
        if (invalidee || !modifiee) {
            return;                       // rien a ecrire : HttpSession n'est pas touche
        }

        HttpSession http = requete.getSession(false);

        for (Map.Entry<String, Object> entree : valeurs.entrySet()) {
            if (http == null) {
                http = requete.getSession(true);   // creation seulement maintenant
            }
            http.setAttribute(entree.getKey(), entree.getValue());
        }

        if (http != null) {
            for (String nom : retirees) {
                http.removeAttribute(nom);
            }
        }

        retirees.clear();
        modifiee = false;
        System.out.println("[SESSION] " + valeurs.size() + " attribut(s) recopie(s) dans HttpSession"
                + (http == null ? " (aucune ecriture : HttpSession non creee)" : ""));
    }

    private static void verifierNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "le nom d'un attribut de session ne peut pas etre vide");
        }
    }
}
