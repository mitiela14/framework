package com.entity;

import com.model.MapFile;


public class Employe {
    private String nom;
    private int age;
    private boolean actif;
    private String poste;
    private Adresse adresse;   

    private MapFile cinRecto;  
    private byte[] scanCIN;    

    public Employe() {}

    public Employe(String nom, int age, boolean actif) {
        this.nom = nom;
        this.age = age;
        this.actif = actif;
    }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }

    public Adresse getAdresse() { return adresse; }
    public void setAdresse(Adresse adresse) { this.adresse = adresse; }

    // --- SPRINT 8 ---
    public MapFile getCinRecto() { return cinRecto; }
    public void setCinRecto(MapFile cinRecto) { this.cinRecto = cinRecto; }

    public byte[] getScanCIN() { return scanCIN; }
    public void setScanCIN(byte[] scanCIN) { this.scanCIN = scanCIN; }

    public String fiche() {
        return "Employe{nom='" + nom + "', age=" + age + ", actif=" + actif + "}";
    }

    @Override
    public String toString() {
        return "Employe{nom='" + nom + "', age=" + age + ", actif=" + actif
                + ", poste='" + poste + "', adresse=" + adresse
                + ", cinRecto=" + cinRecto
                + ", scanCIN=" + (scanCIN == null ? "null" : scanCIN.length + " octets") + "}";
    }
}