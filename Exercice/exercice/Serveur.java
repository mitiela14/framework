package exercice;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Serveur {
    private Contexte contexte;
    private List<ContexteListener> listeners = new ArrayList<>();
    private List<Consumer<Contexte>> filtres = new ArrayList<>();
    private List<Consumer<Contexte>> servlets = new ArrayList<>();

    public void ajouterListener(ContexteListener listener) {
        listeners.add(listener);
    }

    public void ajouterFiltre(Consumer<Contexte> filtre) {
        filtres.add(filtre);
    }

    public void ajouterServlet(Consumer<Contexte> servlet) {
        servlets.add(servlet);
    }

    public void demarrer() {
        System.out.println("Demarrage du serveur...");
        for (ContexteListener listener : listeners) {
            listener.contextInitialized(contexte);
        }
        for (Consumer<Contexte> filtre : filtres) {
            filtre.accept(contexte);
        }
        for (Consumer<Contexte> servlet : servlets) {
            servlet.accept(contexte);
        }
        System.out.println("Serveur demarre avec succes.");
    }

}