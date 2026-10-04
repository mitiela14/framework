package com.exception;

/** SPRINT 2 - Lancee quand une URL saisie n'est associee a aucune methode. */
public class UrlNotFoundException extends Exception {

    public UrlNotFoundException(String urlSaisie, String methodeHttp) {
        super("Erreur 404 - Framework : Aucun controleur ou methode associe a l'URL '"
                + urlSaisie + "' avec la methode HTTP '" + methodeHttp + "'");
    }
}
