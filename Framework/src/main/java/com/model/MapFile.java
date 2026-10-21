package com.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class MapFile {

    private String fileName;      
    private String contentType;   
    private long taille;          
    private byte[] contenu;       

    public MapFile() {
    }

    public MapFile(String fileName, String contentType, byte[] contenu) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.contenu = contenu;
        this.taille = (contenu == null) ? 0L : contenu.length;
    }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public long getTaille() { return taille; }
    public void setTaille(long taille) { this.taille = taille; }

    public byte[] getContenu() { return contenu; }

    /** Le setter met aussi a jour la taille : une seule source de verite. */
    public void setContenu(byte[] contenu) {
        this.contenu = contenu;
        this.taille = (contenu == null) ? 0L : contenu.length;
    }

    /** Vrai si aucun fichier n'a ete choisi (champ vide ou absent). */
    public boolean estVide() {
        return contenu == null || contenu.length == 0
                || fileName == null || fileName.isEmpty();
    }

    /** "cin-recto.png" -> "png" (sans le point). */
    public String getExtension() {
        if (fileName == null) return "";
        int point = fileName.lastIndexOf('.');
        return (point < 0) ? "" : fileName.substring(point + 1);
    }

    public String ecrire(String dossier) throws IOException {
        if (estVide()) {
            throw new IOException("aucun fichier a ecrire (MapFile vide)");
        }
        if (dossier == null || dossier.trim().isEmpty()) {
            throw new IOException("chemin du dossier de sauvegarde inconnu "
                    + "(ServletContext.getRealPath() a renvoye null)");
        }
        return ecrire(dossier, nomSecurise(fileName));
    }

    /** Idem, mais en imposant le nom de destination. */
    public String ecrire(String dossier, String nomFichier) throws IOException {
        File rep = new File(dossier);
        if (!rep.exists() && !rep.mkdirs()) {
            throw new IOException("impossible de creer le dossier de sauvegarde : " + dossier);
        }
        if (nomFichier == null || nomFichier.trim().isEmpty()) {
            nomFichier = "fichier-" + System.currentTimeMillis();
        }
        File destination = new File(rep, nomFichier);
        try (FileOutputStream out = new FileOutputStream(destination)) {
            out.write(contenu);
        }
        System.out.println("[MAPFILE] sauvegarde : " + destination.getAbsolutePath()
                + " (" + contenu.length + " octets)");
        return destination.getAbsolutePath();
    }

    private static String nomSecurise(String nom) {
        String propre = nom.replace('\\', '/');
        int dernier = propre.lastIndexOf('/');
        if (dernier >= 0) propre = propre.substring(dernier + 1);
        propre = propre.replace("..", "");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < propre.length(); i++) {
            char c = propre.charAt(i);
            boolean ok = Character.isLetterOrDigit(c) || c == '.' || c == '_' || c == '-';
            sb.append(ok ? c : '_');
        }
        return sb.toString().trim();
    }

    @Override
    public String toString() {
        if (estVide()) return "MapFile(vide)";
        return "MapFile(fileName='" + fileName + "', contentType='" + contentType
                + "', taille=" + taille + " octets)";
    }
}
