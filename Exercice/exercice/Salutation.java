package exercice;

public class Salutation {
    public void bonjour() {
        System.out.println("Bonjour !");
    }

    public String message() {
        return "Bonjour !";
    }

    public void panne() {
        throw new IllegalArgumentException("Panne volontaire");
    }
}