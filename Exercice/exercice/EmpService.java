package exercice;

import java.util.List;

// SERVICE : la logique metier. Il recoit son repository "de l'exterieur" (injection).
public class EmpService {

    private EmpRepository repository;

    // Le constructeur DEMANDE un EmpRepository : c'est le conteneur qui le fournira
    public EmpService(EmpRepository repository) {
        this.repository = repository;
        System.out.println("      [EmpService] cree");
    }

    public List<Employe> listerEmployes() { return repository.findAll(); }
}
