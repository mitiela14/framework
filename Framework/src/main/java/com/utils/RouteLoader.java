package com.utils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.annotation.UrlMapping;
import com.exception.DuplicateUrlException;
import com.model.Mapping;


public class RouteLoader {

    public static Map<String, Mapping> buildRoutes(List<String> controllers) throws Exception {

        // TreeMap : meme principe qu'une HashMap, mais triee par URL (affichage stable)
        Map<String, Mapping> routes = new TreeMap<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        for (String className : controllers) {
            Class<?> cls = Class.forName(className, false, classLoader);

            // On regarde TOUTES les methodes de la classe
            for (Method method : cls.getDeclaredMethods()) {

                // Seules celles qui portent @UrlMapping nous interessent
                if (!method.isAnnotationPresent(UrlMapping.class)) continue;

                UrlMapping annotation = method.getAnnotation(UrlMapping.class);
                String url = normalize(annotation.value());

                // Cette URL est-elle deja prise par une autre methode ?
                if (routes.containsKey(url)) {
                    throw new DuplicateUrlException(url, routes.get(url));
                }

                routes.put(url, new Mapping(cls.getName(), method.getName()));
            }
        }
        return routes;
    }

    // "emp/list" et "/emp/list" doivent etre equivalents :
    // l'URL saisie, elle, commence toujours par "/"
    private static String normalize(String url) {
        url = url.trim();
        if (!url.startsWith("/")) url = "/" + url;
        return url;
    }
}
