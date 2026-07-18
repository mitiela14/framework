package exercice;

// import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Main {

//     // Ce que l'on retient pour chaque URL : la classe et la methode
//     static class Mapping {
//         String className;
//         String methodName;
//         Mapping(String className, String methodName) {
//             this.className = className;
//             this.methodName = methodName;
//         }
//         public String toString() { return className + "." + methodName + "()"; }
//     }

//     // ETAPE 1 : construire la table  URL -> Mapping
//     static Map<String, Mapping> construire(Class<?>... classes) {
//         Map<String, Mapping> table = new TreeMap<>();
//         for (Class<?> cls : classes) {
//             if (!cls.isAnnotationPresent(Controleur.class)) continue;   // pas un controleur : on saute

//             for (Method m : cls.getDeclaredMethods()) {
//                 if (m.isAnnotationPresent(UrlMapping.class)) {
//                     String url = m.getAnnotation(UrlMapping.class).value();   // lire la "variable"
//                     table.put(url, new Mapping(cls.getName(), m.getName()));
//                 }
//             }
//         }
//         return table;
//     }

//     // ETAPE 2 : chercher une URL ; si absente => on LANCE une exception
//     static Mapping trouver(Map<String, Mapping> table, String url) throws Exception {
//         Mapping m = table.get(url);
//         if (m == null) {
//             throw new Exception("URL non associee : " + url);
//         }
//         return m;
//     }

//     public static void main(String[] args) {
//         Map<String, Mapping> table = construire(EmpController.class, Divers.class);
//         System.out.println("Table : " + table + "\n");

//         String[] essais = { "/emp/list", "/emp/new", "/divers/test", "/emp/zzz" };
//         for (String url : essais) {
//             try {
//                 System.out.println(url + "  ->  " + trouver(table, url));
//             } catch (Exception e) {
//                 System.out.println(url + "  ->  ERREUR : " + e.getMessage());
//             }
//         }
//     }

    public static void main(String[] args) {

        // ---------- 1. SANS equals/hashCode
        System.out.println("Cle SANS equals");
        Map<CleSansEquals, String> m1 = new HashMap<>();
        m1.put(new CleSansEquals("/emp/list", "GET"), "EmpController.liste");

        CleSansEquals recherche1 = new CleSansEquals("/emp/list", "GET");   // meme contenu, autre objet
        System.out.println("get(meme contenu)         = " + m1.get(recherche1));
        System.out.println("containsKey(meme contenu) = " + m1.containsKey(recherche1));

        // Un doublon n'est PAS detecte : la 2e entree s'ajoute
        m1.put(new CleSansEquals("/emp/list", "GET"), "Autre.methode");
        System.out.println("taille apres doublon      = " + m1.size() + "   <- doublon non detecte !");

        // ---------- 2. AVEC equals/hashCode
        System.out.println("\n=== 2. Cle AVEC equals/hashCode");
        Map<CleAvecEquals, String> m2 = new HashMap<>();
        m2.put(new CleAvecEquals("/emp/list", "GET"), "EmpController.liste");

        CleAvecEquals recherche2 = new CleAvecEquals("/emp/list", "get");  // "get" -> "GET"
        System.out.println("get(meme contenu)         = " + m2.get(recherche2));
        System.out.println("containsKey(meme contenu) = " + m2.containsKey(recherche2)
                + "   <- ici on pourrait lancer l'exception de doublon");

        // ---------- 3. Meme URL, methodes differentes = cles differentes
        System.out.println("\n=== 3. Meme URL, GET et POST");
        m2.put(new CleAvecEquals("/emp/new", "GET"), "EmpController.create");
        m2.put(new CleAvecEquals("/emp/new", "POST"), "EmpController.enregistrer");
        System.out.println("taille                    = " + m2.size());
        System.out.println("get([GET]  /emp/new)      = " + m2.get(new CleAvecEquals("/emp/new", "GET")));
        System.out.println("get([POST] /emp/new)      = " + m2.get(new CleAvecEquals("/emp/new", "POST")));
        System.out.println("get([POST] /emp/list)     = " + m2.get(new CleAvecEquals("/emp/list", "POST"))
                + "   <- null => 404");    
    }
}




