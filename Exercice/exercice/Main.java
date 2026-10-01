package exercice;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

public class Main {

    // Ce que l'on retient pour chaque URL : la classe et la methode
    static class Mapping {
        String className;
        String methodName;
        Mapping(String className, String methodName) {
            this.className = className;
            this.methodName = methodName;
        }
        public String toString() { return className + "." + methodName + "()"; }
    }

    // ETAPE 1 : construire la table  URL -> Mapping
    static Map<String, Mapping> construire(Class<?>... classes) {
        Map<String, Mapping> table = new TreeMap<>();
        for (Class<?> cls : classes) {
            if (!cls.isAnnotationPresent(Controleur.class)) continue;   // pas un controleur : on saute

            for (Method m : cls.getDeclaredMethods()) {
                if (m.isAnnotationPresent(UrlMapping.class)) {
                    String url = m.getAnnotation(UrlMapping.class).value();   // lire la "variable"
                    table.put(url, new Mapping(cls.getName(), m.getName()));
                }
            }
        }
        return table;
    }

    // ETAPE 2 : chercher une URL ; si absente => on LANCE une exception
    static Mapping trouver(Map<String, Mapping> table, String url) throws Exception {
        Mapping m = table.get(url);
        if (m == null) {
            throw new Exception("URL non associee : " + url);
        }
        return m;
    }

    public static void main(String[] args) {
        Map<String, Mapping> table = construire(EmpController.class, Divers.class);
        System.out.println("Table : " + table + "\n");

        String[] essais = { "/emp/list", "/emp/new", "/divers/test", "/emp/zzz" };
        for (String url : essais) {
            try {
                System.out.println(url + "  ->  " + trouver(table, url));
            } catch (Exception e) {
                System.out.println(url + "  ->  ERREUR : " + e.getMessage());
            }
        }
    }
}
