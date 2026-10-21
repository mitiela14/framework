package com.entity;


public class Adresse {
    private String ville;
    private String quartier;

    public Adresse() {}

    public Adresse(String ville, String quartier) {
        this.ville = ville;
        this.quartier = quartier;
    }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getQuartier() { return quartier; }
    public void setQuartier(String quartier) { this.quartier = quartier; }

    @Override
    public String toString() {
        return "Adresse{ville='" + ville + "', quartier='" + quartier + "'}";
    }
}