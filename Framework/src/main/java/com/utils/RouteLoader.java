package com.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.annotation.UrlMapping;
import com.exception.DuplicateUrlException;
import com.model.Mapping;
import com.model.UrlMethod;

public class RouteLoader {

    public static Map<UrlMethod, Mapping> buildRoutes(List<String> controllers) throws Exception {

        // HashMap : elle utilise equals() et hashCode() de UrlMethod pour retrouver une cle
        Map<UrlMethod, Mapping> routes = new HashMap<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        for (String className : controllers) {
            Class<?> cls = Class.forName(className, false, classLoader);

            for (Method method : cls.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(UrlMapping.class)) continue;

                UrlMapping annotation = method.getAnnotation(UrlMapping.class);
                String url = normalize(annotation.value());
                String httpMethod = annotation.method().trim().toUpperCase();

                // Seules GET et POST sont acceptees
                if (!httpMethod.equals("GET") && !httpMethod.equals("POST")) {
                    throw new IllegalArgumentException("Methode HTTP '" + annotation.method()
                            + "' non supportee sur " + cls.getName() + "." + method.getName()
                            + "() : utiliser GET ou POST");
                }

                // La cle = URL + methode HTTP
                UrlMethod key = new UrlMethod(url, httpMethod);

                // Deja presente ? => meme URL ET meme methode HTTP => conflit
                if (routes.containsKey(key)) {
                    throw new DuplicateUrlException(url, httpMethod, routes.get(key));
                }

                routes.put(key, new Mapping(cls.getName(), method.getName()));
            }
        }
        return routes;
    }

    // Une HashMap n'a pas d'ordre : on trie seulement pour l'AFFICHAGE (par URL puis methode)
    public static List<Map.Entry<UrlMethod, Mapping>> sorted(Map<UrlMethod, Mapping> routes) {
        List<Map.Entry<UrlMethod, Mapping>> list = new ArrayList<>(routes.entrySet());
        list.sort(Comparator
                .comparing((Map.Entry<UrlMethod, Mapping> e) -> e.getKey().getUrl())
                .thenComparing(e -> e.getKey().getMethod()));
        return list;
    }

    private static String normalize(String url) {
        url = url.trim();
        if (!url.startsWith("/")) url = "/" + url;
        return url;
    }
}
