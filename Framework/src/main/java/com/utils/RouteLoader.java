package com.utils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.annotation.AnnotationController;
import com.annotation.UrlMapping;
import com.exception.DuplicateUrlException;
import com.model.Mapping;
import com.model.UrlMethod;


public class RouteLoader {

    public static void buildRoutingTable(String packageName, Map<UrlMethod, Mapping> mappingUrls) throws Exception {

        List<Class<?>> classes = PackageScanner.scanClasses(packageName);
        int nombreRoutes = 0;

        for (Class<?> cls : classes) {
            if (!cls.isAnnotationPresent(AnnotationController.class)) continue;

            for (Method method : cls.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(UrlMapping.class)) continue;

                UrlMapping urlMapping = method.getAnnotation(UrlMapping.class);

                String urlValue = urlMapping.value();
                String httpMethod = urlMapping.method().toUpperCase();

                UrlMethod urlMethodKey = new UrlMethod(urlValue, httpMethod);

                if (mappingUrls.containsKey(urlMethodKey)) {
                    throw new DuplicateUrlException(urlValue, httpMethod);
                }

                mappingUrls.put(urlMethodKey, new Mapping(cls.getName(), method.getName()));
                nombreRoutes++;
                System.out.println("[ROUTING] " + httpMethod + " " + urlValue
                        + "  ->  " + cls.getSimpleName() + "." + method.getName() + "()");
            }
        }

        System.out.println("[ROUTING] " + nombreRoutes + " route(s) enregistree(s) pour le package " + packageName);
    }
}