package exercice;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Serveur {

    private Contexte contexte = new Contexte();
    private List<ContexteListener> listeners = new ArrayList<>();
    private List<Consumer<Contexte>> filtres = new ArrayList<>();
    private List<Consumer<Contexte>> servlets = new ArrayList<>();

    public void ajouterListener(ContexteListener l)       { listeners.add(l); }
    public void ajouterFiltre(Consumer<Contexte> f)       { filtres.add(f); }
    public void ajouterServlet(Consumer<Contexte> s)      { servlets.add(s); }

    public void demarrer() {
        System.out.println("--- demarrage de l'application ---");
        for (ContexteListener l : listeners) l.contextInitialized(contexte);   // 1. listeners
        for (Consumer<Contexte> f : filtres) f.accept(contexte);               // 2. filtres
        for (Consumer<Contexte> s : servlets) s.accept(contexte);              // 3. servlets
        System.out.println("--- application prete ---\n");
    }
}
