package exercice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    static String prefixe = "/WEB-INF/Views/";
    static String suffixe = ".jsp";

    static ModelView afficherLesFruits() {
        List<String> fruits = new ArrayList<>();
        fruits.add("Pomme");
        fruits.add("Banane");

        ModelView mv = new ModelView("ListeFruits");
        mv.addAttribute("titre", "Fruits de la saison");
        mv.addAttribute("fruits", fruits);
        return mv;
    }

    static String uneChaine() { return "Liste des employes"; }

    static void rienDuTout()  { }

    // ----- Ce que fait le servlet du Sprint 5 avec le resultat
    static void traiter(Object result) {
        Requete request = new Requete();

        if (result instanceof ModelView) {                       // 1. est-ce un ModelView ?
            ModelView mv = (ModelView) result;                   //    oui : on le "convertit"

            for (Map.Entry<String, Object> e : mv.getData().entrySet()) {
                request.setAttribute(e.getKey(), e.getValue());  // 2. donnees -> attributs de requete
            }

            String chemin = prefixe + mv.getUrl() + suffixe;     // 3. concatener
            request.forward(chemin);                             // 4. forward

        } else {
            System.out.println("   pas de vue, valeur brute = " + result);
        }
    }

    public static void main(String[] args) {
        System.out.println("1) la methode retourne un ModelView");
        traiter(afficherLesFruits());

        System.out.println("\n2) la methode retourne une chaine");
        traiter(uneChaine());

        System.out.println("\n3) la methode est void (invoke renverrait null)");
        traiter(null);

        System.out.println("\n4) null est-il un ModelView ?  " + (null instanceof ModelView));
    }
}
