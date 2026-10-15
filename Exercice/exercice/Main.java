package exercice;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        Serveur serveur = new Serveur();

        serveur.ajouterListener(contexte -> {
            System.out.println("[Listener] je construis la table des routes");
            Map<String, String> routes = new HashMap<>();
            routes.put("/emp/list", "EmpController.liste");
            contexte.setAttribute("mesRoutes", routes);
        });

        serveur.ajouterFiltre(contexte -> System.out.println("[Filtre]   init()"));

        serveur.ajouterServlet(contexte -> {
            @SuppressWarnings("unchecked")
            Map<String, String> routes = (Map<String, String>) contexte.getAttribute("mesRoutes");
            System.out.println("[Servlet]  init() : routes recuperees = " + routes);
        });

        serveur.demarrer();

        Serveur oubli = new Serveur();
        oubli.ajouterServlet(contexte -> {
            Object routes = contexte.getAttribute("mesRoutes");
            if (routes == null) {
                System.out.println("[Servlet]  ERREUR : le registre des routes n'a pas ete initialise au demarrage");
            }
        });
        oubli.demarrer();
    }
}
