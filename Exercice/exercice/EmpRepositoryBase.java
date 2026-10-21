package exercice;

import java.util.ArrayList;
import java.util.List;

// Une AUTRE facon (imaginons MySQL) : meme contrat, autre implementation
public class EmpRepositoryBase implements EmpRepository {

    public EmpRepositoryBase() {
        System.out.println("      [EmpRepositoryBase] cree (ici, on irait lire MySQL)");
    }

    public List<Employe> findAll() {
        List<Employe> liste = new ArrayList<>();
        liste.add(new Employe(10, "Rakoto"));
        liste.add(new Employe(11, "Rabe"));
        liste.add(new Employe(12, "Rasoa"));
        return liste;
    }
}
