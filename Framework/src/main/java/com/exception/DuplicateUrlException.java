package com.exception;

import com.model.Mapping;

/** SPRINT 2 - Lancee au demarrage si la meme URL est declaree sur deux methodes. */
public class DuplicateUrlException extends Exception {

    public DuplicateUrlException(String url, Mapping dejaAssocie) {
        super("Erreur Framework : Conflit de routage ! L'URL '" + url
                + "' est deja associee a " + dejaAssocie);
    }
}
