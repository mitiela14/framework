package exercice;

import java.util.List;

// REPOSITORY : le "contrat" d'acces aux donnees. Le service ne connait QUE ce contrat.
public interface EmpRepository {
    List<Employe> findAll();
}
