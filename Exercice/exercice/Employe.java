package exercice;

// ENTITE : un employe (une ligne de la table)
public class Employe {
    private int id;
    private String nom;

    public Employe(int id, String nom) { this.id = id; this.nom = nom; }

    @Override
    public String toString() { return id + "-" + nom; }
}
