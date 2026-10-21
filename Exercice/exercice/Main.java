package exercice;

public class Main {

    public static void main(String[] args) {

        // ===== 1. SANS conteneur : on fabrique tout a la main, A CHAQUE REQUETE (= prototype)
        System.out.println("=== 1. Sans conteneur : new a chaque requete");
        for (int requete = 1; requete <= 2; requete++) {
            System.out.println("   requete " + requete + " :");
            EmpController c = new EmpController(new EmpService(new EmpRepositoryMemoire()));
            System.out.println("   -> " + c.liste());
        }

        // ===== 2. AVEC conteneur : tout est cree UNE FOIS (singleton)
        System.out.println("\n=== 2. Avec conteneur : singletons");
        MiniConteneur conteneur = new MiniConteneur();
        conteneur.enregistrer(EmpRepository.class, EmpRepositoryMemoire.class);

        for (int requete = 1; requete <= 3; requete++) {
            System.out.println("   requete " + requete + " :");
            EmpController c = conteneur.getBean(EmpController.class);
            System.out.println("   -> " + c.liste());
        }

        // ===== 3. Changer la source des donnees : UNE SEULE ligne change
        System.out.println("\n=== 3. On remplace le repository (memoire -> base) : ni le service ni le controleur ne changent");
        MiniConteneur conteneur2 = new MiniConteneur();
        conteneur2.enregistrer(EmpRepository.class, EmpRepositoryBase.class);     // <-- la seule ligne modifiee
        System.out.println("   -> " + conteneur2.getBean(EmpController.class).liste());

        // ===== 4. Scope prototype : le controleur est recree a chaque demande
        System.out.println("\n=== 4. Controleur en prototype (le service et le repository restent des singletons)");
        MiniConteneur conteneur3 = new MiniConteneur();
        conteneur3.enregistrer(EmpRepository.class, EmpRepositoryMemoire.class);
        conteneur3.enPrototype(EmpController.class);
        for (int requete = 1; requete <= 2; requete++) {
            System.out.println("   requete " + requete + " :");
            System.out.println("   -> " + conteneur3.getBean(EmpController.class).liste());
        }
    }
}
