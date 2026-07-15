package com.utils;

import com.annotation.AnnotationController;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * SPRINT 1 - Parcourt un ou plusieurs packages et retourne le nom complet
 * de toutes les classes annotees @AnnotationController.
 */
public class ControllerScanner {

    /**
     * @param packages un ou plusieurs packages separes par des virgules (ex: "com.controller,com.autre")
     * @return la liste des noms de classes controleurs (ex: "com.controller.EmpController")
     */
    public static List<String> findControllers(String packages) throws Exception {
        List<String> controllers = new ArrayList<>();

        for (String packageName : packages.split(",")) {
            packageName = packageName.trim();
            if (packageName.isEmpty()) continue;

            // com.controller -> com/controller
            String path = packageName.replace('.', '/');
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            // Ou se trouve physiquement ce package ? (WEB-INF/classes/com/controller)
            // getResources (au pluriel) : le meme package peut exister a plusieurs endroits.
            // Ex : "com/controller" existe dans WEB-INF/classes (tes controleurs)
            // ET dans framework.jar (FrontControllerServlet).
            boolean dossierTrouve = false;
            Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();

                // On ne garde que les vrais dossiers (protocole "file"), pas l'interieur d'un .jar
                if ("file".equals(resource.getProtocol())) {
                    File directory = new File(resource.toURI());
                    scanDirectory(directory, packageName, classLoader, controllers);
                    dossierTrouve = true;
                }
            }

            if (!dossierTrouve) {
                throw new Exception("Package introuvable : " + packageName);
            }
        }
        return controllers;
    }

    // Parcours recursif : dossiers -> sous-packages, fichiers .class -> classes a tester
    private static void scanDirectory(File directory, String packageName,
                                      ClassLoader classLoader, List<String> controllers)
            throws ClassNotFoundException {

        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), classLoader, controllers);

            } else if (file.getName().endsWith(".class")) {
                // EmpController.class -> EmpController
                String simpleName = file.getName().substring(0, file.getName().length() - 6);
                String className = packageName + "." + simpleName;

                // false = on charge la classe sans executer ses blocs static
                Class<?> cls = Class.forName(className, false, classLoader);

                // L'annotation est-elle presente sur la classe ?
                if (cls.isAnnotationPresent(AnnotationController.class)) {
                    controllers.add(cls.getName());
                }
            }
        }
    }
}
