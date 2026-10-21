package com.utils;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public final class PackageScanner {

    private PackageScanner() {
    }

    /**
     * Renvoie la liste des NOMS COMPLETS des classes du package et de ses
     * sous-packages (ex : "com.controller.FruitController").
     *
     * @param packageName 
     */
    public static List<String> scanClassNames(String packageName) {
        Set<String> noms = new LinkedHashSet<>();
        String path = packageName.replace('.', '/');

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) classLoader = PackageScanner.class.getClassLoader();

        try {
            Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                String protocole = resource.getProtocol();

                if ("file".equalsIgnoreCase(protocole)) {
                    scanDossier(urlEnChemin(resource), path, noms);
                } else if ("jar".equalsIgnoreCase(protocole)) {
                    scanJar(resource, path, noms);
                }
            }
        } catch (IOException e) {
            System.out.println("[SCAN] Impossible de scanner le package " + packageName + " : " + e.getMessage());
        }

        return new ArrayList<>(noms);
    }

    public static List<Class<?>> scanClasses(String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        for (String nom : scanClassNames(packageName)) {
            try {
                classes.add(Class.forName(nom, false,
                        Thread.currentThread().getContextClassLoader()));
            } catch (ClassNotFoundException | LinkageError e) {
                System.out.println("[SCAN] Classe ignoree (" + nom + ") : " + e);
            }
        }
        return classes;
    }

    private static void scanDossier(String chemin, String prefixe, Set<String> noms) {
        File racine = new File(chemin);
        if (!racine.isDirectory()) return;
        scannerFichier(racine, prefixe, noms);
    }

    private static void scannerFichier(File dossier, String prefixe, Set<String> noms) {
        File[] elements = dossier.listFiles();
        if (elements == null) return;

        for (File element : elements) {
            if (element.isDirectory()) {
                scannerFichier(element, prefixe + "/" + element.getName(), noms);
            } else if (element.getName().endsWith(".class")) {
                String simple = element.getName().substring(0, element.getName().length() - 6);
                noms.add(prefixe.replace('/', '.') + "." + simple);
            }
        }
    }

    private static void scanJar(URL resource, String prefixe, Set<String> noms) {
        JarFile jar = null;
        boolean fermer = false;
        try {
            JarURLConnection connexion = (JarURLConnection) resource.openConnection();
            connexion.setUseCaches(false); // on veut notre propre JarFile, a nous
            jar = connexion.getJarFile();
            fermer = true;

            String prefixeSlash = prefixe.endsWith("/") ? prefixe : prefixe + "/";
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String nom = entry.getName();

                if (entry.isDirectory() || !nom.endsWith(".class")) continue;
                if (!nom.startsWith(prefixeSlash)) continue;

                String nomComplet = nom.substring(0, nom.length() - 6).replace('/', '.');
                noms.add(nomComplet);
            }
        } catch (IOException e) {
            System.out.println("[SCAN] Impossible de lire le JAR " + resource + " : " + e.getMessage());
        } finally {
            if (fermer && jar != null) {
                try {
                    jar.close();
                } catch (IOException ignore) {
                    // rien a faire
                }
            }
        }
    }

    private static String urlEnChemin(URL resource) {
        try {
            return URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            return resource.getFile();
        }
    }
}