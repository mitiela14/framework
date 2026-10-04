package exercice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Main {

    static void executer(String className, String methodName) {
        System.out.println("--- " + className + "." + methodName + "()");
        try {
            Class<?> cls = Class.forName(className);                 // 1. nom -> Class
            Object instance = cls.getDeclaredConstructor().newInstance(); // 2. new dynamique
            Method m = cls.getDeclaredMethod(methodName);            // 3. nom -> Method
            Object result = m.invoke(instance);                      // 4. execution
            System.out.println("    retour = " + result);
        } catch (InvocationTargetException e) {
            // la methode appelee a lance une exception : elle est emballee
            System.out.println("    La methode a LANCE : " + e.getCause());
            System.out.println("    (e.getMessage() = " + e.getMessage() + "   <- vide : l'erreur est dans getCause())");
        } catch (ReflectiveOperationException e) {
            System.out.println("    Impossible d'invoquer : " + e);
        }
    }

    public static void main(String[] args) {
        executer("exercice.Salutation", "bonjour");        // void : retour = null
        executer("exercice.Salutation", "message");        // retourne un String
        executer("exercice.Salutation", "panne");          // la methode plante
        executer("exercice.Salutation", "inexistante");    // methode qui n'existe pas
        executer("exercice.Inconnue", "bonjour");          // classe qui n'existe pas
    }
}
