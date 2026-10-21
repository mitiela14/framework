package exercice;

import java.util.ArrayList;
import java.util.List;

// Une premiere facon de fournir les donnees : en memoire (comme les listes "en dur")
public class EmpRepositoryMemoire implements EmpRepository {

    public EmpRepositoryMemoire() {
        System.out.println("      [EmpRepositoryMemoire] cree");
    }

    public List<Employe> findAll() {
        List<Employe> liste = new ArrayList<>();
        liste.add(new Employe(1, "Nirisoa"));
        liste.add(new Employe(2, "Valisoa"));
        return liste;
    }
}
