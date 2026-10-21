package com.binding;

import com.exception.BindingException;
import com.model.MapFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;


public class MultipartReader {

    private final HttpServletRequest request;

    private Map<String, Part> parties;

    private final Map<String, MapFile> fichiers = new LinkedHashMap<>();
    private final Map<String, String> textes = new LinkedHashMap<>();

    public MultipartReader(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public boolean estMultipart() {
        String contentType = request.getContentType();
        return contentType != null
                && contentType.toLowerCase(Locale.ROOT).startsWith("multipart/form-data");
    }

    // CHARGEMENT DES PARTS

    private Map<String, Part> parties() throws BindingException {
        if (parties != null) return parties;
        if (!estMultipart()) {
            parties = Collections.emptyMap();
            return parties;
        }
        try {
            Collection<Part> liste = request.getParts();
            Map<String, Part> parNom = new LinkedHashMap<>();
            for (Part part : liste) {
                if (parNom.containsKey(part.getName())) {
                    System.out.println("[MULTIPART] champ '" + part.getName()
                            + "' present plusieurs fois : seul le premier est utilise");
                    continue;
                }
                parNom.put(part.getName(), part);
            }
            parties = parNom;
        } catch (IllegalStateException e) {
            // request.getParts() exige @MultipartConfig / <multipart-config>
            throw new BindingException(
                    "la requete est multipart mais le servlet FrontController n'a pas de "
                    + "configuration multipart. Ajoute @MultipartConfig sur la servlet "
                    + "ou <multipart-config> dans web.xml.", e);
        } catch (IOException | ServletException e) {
            throw new BindingException("impossible de lire les donnees multipart "
                    + "de la requete : " + e.getMessage(), e);
        }
        return parties;
    }

    /** Le Part du champ "nomComplet" sinon celui de "nomCourt", sinon null. */
    private Part partie(String nomComplet, String nomCourt) throws BindingException {
        Map<String, Part> toutes = parties();
        Part part = toutes.get(nomComplet);
        if (part == null && nomCourt != null && !nomCourt.equals(nomComplet)) {
            part = toutes.get(nomCourt);
        }
        return part;
    }

    // LES 3 LECTURES DEMANDEES PAR LE BINDING

    public MapFile lireFichier(String nomComplet, String nomCourt) throws BindingException {
        if (!estMultipart()) return null;

        Part part = partie(nomComplet, nomCourt);
        if (part == null) return null;

        String nomFichier = part.getSubmittedFileName();
        if (nomFichier == null || nomFichier.isEmpty()) return null;   // pas un fichier

        String cle = (parties().containsKey(nomComplet)) ? nomComplet : nomCourt;
        MapFile dejaLu = fichiers.get(cle);
        if (dejaLu != null) return dejaLu;

        byte[] octets = lireOctets(part);
        MapFile fichier = new MapFile(nomFichier, part.getContentType(), octets);
        fichiers.put(cle, fichier);

        System.out.println("[MULTIPART] fichier '" + cle + "' -> " + nomFichier
                + " (" + (part.getContentType() == null ? "type inconnu" : part.getContentType())
                + ", " + octets.length + " octets)");
        return fichier;
    }

    public byte[] lireOctets(String nomComplet, String nomCourt) throws BindingException {
        MapFile fichier = lireFichier(nomComplet, nomCourt);
        return (fichier == null) ? null : fichier.getContenu();
    }


    public String lireTexte(String nom) throws BindingException {
        String brut = request.getParameter(nom);
        if (brut != null) return brut;
        if (!estMultipart()) return null;

        Part part = partie(nom, nom);
        if (part == null) return null;

        String nomFichier = part.getSubmittedFileName();
        if (nomFichier != null && !nomFichier.isEmpty()) {
            return null;    // c'est un fichier, pas une valeur texte
        }
        String dejaLu = textes.get(nom);
        if (dejaLu != null) return dejaLu;

        String valeur = new String(lireOctets(part), encodage());
        textes.put(nom, valeur);
        return valeur;
    }

    // OUTILS

    private static byte[] lireOctets(Part part) throws BindingException {
        try (InputStream in = part.getInputStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] tampon = new byte[8192];
            int lu;
            while ((lu = in.read(tampon)) != -1) {
                out.write(tampon, 0, lu);
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new BindingException("impossible de lire le contenu du champ '"
                    + part.getName() + "' : " + e.getMessage(), e);
        }
    }

    /** L'encodage des champs texte (UTF-8 par defaut, comme les JSP). */
    private Charset encodage() {
        String enc = request.getCharacterEncoding();
        if (enc == null || enc.trim().isEmpty()) return StandardCharsets.UTF_8;
        try {
            return Charset.forName(enc);
        } catch (RuntimeException e) {
            return StandardCharsets.UTF_8;
        }
    }

    /** Trace de TOUT ce que la requete contient (texte + fichiers). */
    public void afficherRecu() {
        if (!estMultipart()) return;
        System.out.println("[MULTIPART] requete multipart/form-data (encodage "
                + encodage() + ")");
        try {
            for (Map.Entry<String, Part> e : parties().entrySet()) {
                Part part = e.getValue();
                String nomFichier = part.getSubmittedFileName();
                if (nomFichier != null && !nomFichier.isEmpty()) {
                    System.out.println("[MULTIPART]   fichier: " + e.getKey()
                            + " = " + nomFichier + " (" + part.getSize() + " octets)");
                } else {
                    String valeur = lireTexte(e.getKey());
                    System.out.println("[MULTIPART]   champ: " + e.getKey()
                            + " = \"" + (valeur == null ? "" : valeur) + "\"");
                }
            }
        } catch (BindingException e) {
            System.out.println("[MULTIPART][ERREUR] " + e.getMessage());
        }
    }

    /** Nombre de fichiers recus (utile pour les traces du controleur). */
    public int nombreDeFichiers() {
        return fichiers.size();
    }
}
