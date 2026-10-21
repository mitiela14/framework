package exercice;

import java.util.List;

// CONTROLEUR : recoit la requete, demande les donnees au service
public class EmpController {

    private EmpService service;
    private int compteur = 0;     // etat : montre la difference singleton / prototype

    public EmpController(EmpService service) {
        this.service = service;
        System.out.println("      [EmpController] cree : @" + Integer.toHexString(System.identityHashCode(this)));
    }

    public String liste() {
        compteur++;
        List<Employe> employes = service.listerEmployes();
        return "instance @" + Integer.toHexString(System.identityHashCode(this))
                + ", appel numero " + compteur + ", employes = " + employes;
    }
}
