package com.utils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;

import com.annotation.UrlMapping;
import com.exception.DuplicateUrlException;
import com.model.Mapping;
import com.model.UrlMethod;


public class RouteLoader {

    public static Map<UrlMethod, Mapping> buildRoutes(List<String> controllers) throws Exception {

        // TreeMap : meme principe qu'une HashMap, mais triee par URL (affichage stable)
        Map<UrlMethod, Mapping> routes = new HashMap<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        for (String className : controllers) {
            Class<?> cls = Class.forName(className, false, classLoader);

            // On regarde TOUTES les methodes de la classe
            for (Method method : cls.getDeclaredMethods()) {

                // Seules celles qui portent @UrlMapping nous interessent
                if (!method.isAnnotationPresent(UrlMapping.class)) continue;

                UrlMapping annotation = method.getAnnotation(UrlMapping.class);
                String url = normalize(annotation.value());
                String httpMethod = annotation.method().trim().toUpperCase();

                if(!httpMethod.equals("GET") && !httpMethod.equals("POST")) {
                    throw new IllegalArgumentException("Methode http non supportee: "
                     + httpMethod);
                }

                UrlMethod key = new UrlMethod(url, httpMethod);

                // Cette URL est-elle deja prise par une autre methode ?
                if (routes.containsKey(key)) {
                    throw new DuplicateUrlException(url,httpMethod, routes.get(key));
                }

                routes.put(key, new Mapping(cls.getName(), method.getName()));
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
