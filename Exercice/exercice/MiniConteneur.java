package exercice;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Un mini "Spring" en 30 lignes : il cree les objets, leur donne ce dont ils ont besoin
 * (injection par le constructeur) et garde UN SEUL exemplaire de chacun (singleton).
 */
public class MiniConteneur {

    private Map<Class<?>, Class<?>> implementations = new HashMap<>();  // interface -> classe choisie
    private Map<Class<?>, Object> singletons = new HashMap<>();          // les objets deja crees
    private Set<Class<?>> prototypes = new HashSet<>();                  // types a recreer a chaque fois

    public void enregistrer(Class<?> type, Class<?> implementation) { implementations.put(type, implementation); }
    public void enPrototype(Class<?> type)                          { prototypes.add(type); }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> type) {
        boolean prototype = prototypes.contains(type);

        if (!prototype && singletons.containsKey(type)) {
            return (T) singletons.get(type);                // deja cree : on RENVOIE le meme
        }

        Class<?> classeReelle = implementations.containsKey(type) ? implementations.get(type) : type;
        Object bean = creer(classeReelle);

        if (!prototype) singletons.put(type, bean);          // singleton : on le garde
        return (T) bean;
    }

    // Cree un objet en fournissant automatiquement chaque parametre de son constructeur
    private Object creer(Class<?> classe) {
        try {
            Constructor<?> constructeur = classe.getConstructors()[0];
            Class<?>[] types = constructeur.getParameterTypes();
            Object[] arguments = new Object[types.length];
            for (int i = 0; i < types.length; i++) {
                arguments[i] = getBean(types[i]);            // on demande au conteneur chaque dependance
            }
            return constructeur.newInstance(arguments);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Impossible de creer " + classe.getName(), e);
        }
    }
}
