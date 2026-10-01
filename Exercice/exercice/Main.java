package exercice;
 

import java.lang.reflect.Field;
import java.lang.reflect.Method;
public class Main {

    public static void main(String[] args) throws Exception{
        Class<?> cls = Etudiant.class;


        System.out.println("== Classe " + cls.getSimpleName());
        System.out.println("@MonAnnotation presente ? " + cls.isAnnotationPresent(MonAnnotation.class));
        System.out.println("@Invisible presente ?     " + cls.isAnnotationPresent(Invisible.class)
                           + "   (Retention.CLASS => invisible a l'execution)");

        // ---- 4. RECUPERATION de l'annotation et de ses valeurs
        MonAnnotation a = cls.getAnnotation(MonAnnotation.class);
        System.out.println("value = " + a.value() + " | priorite = " + a.priorite());

        // ---- Sur les ATTRIBUTS
        System.out.println("\n== Attributs");
        for (Field f : cls.getDeclaredFields()) {
            if (f.isAnnotationPresent(MonAnnotation.class)) {
                MonAnnotation fa = f.getAnnotation(MonAnnotation.class);
                System.out.println(f.getName() + " -> OUI : " + fa.value() + " (priorite " + fa.priorite() + ")");
            } else {
                System.out.println(f.getName() + " -> non");
            }
        }

        // ---- Sur les METHODES
        System.out.println("\n== Methodes");
        for (Method m : cls.getDeclaredMethods()) {
            if (m.isAnnotationPresent(MonAnnotation.class)) {
                MonAnnotation ma = m.getAnnotation(MonAnnotation.class);
                System.out.println(m.getName() + " -> OUI : " + ma.value() + " (priorite " + ma.priorite() + ")");
            } else {
                System.out.println(m.getName() + " -> non");
            }
        }
    }
}
