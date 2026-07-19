package exercice;

import java.util.HashMap;
import java.util.Map;

public class Requete {
    private Map<String, Object> attributs = new HashMap<>();

    public void setAttribute(String nom, Object valeur) {
        attributs.put(nom, valeur);
    }
    
    public Object getAttribute(String nom) {
        return attributs.get(nom);
    }

    public void forward(String chemin) {
        System.out.println("[FORWARD] vers " + chemin);
        System.out.println(" la vue peut lire :" + attributs);
    }
}