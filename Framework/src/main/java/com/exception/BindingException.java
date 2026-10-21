package com.exception;

public class BindingException extends Exception {

    public BindingException(String message) {
        super(message);
    }

    public BindingException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getMessageComplet() {
        return "Erreur de binding (Sprint 7 / 7bis) : " + getMessage();
    }
}