package com.binding;

import com.exception.BindingException;
import com.model.MapFile;
import com.session.MySession;
import com.session.SessionManager;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


public class ParameterBinder {

    private static final String[] VIDE = new String[0];

    public static BindingResult bind(Method method,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws BindingException {

        
        Parameter[] params = method.getParameters();

        Object[] arguments = new Object[params.length];
        BindingResult resultat = new BindingResult(arguments);

        // SPRINT 8 : un seul lecteur pour toute la requete. Il sait lire
        // aussi bien un formulaire classique (getParameter) qu'un formulaire
        // multipart avec des fichiers (getParts).
        MultipartReader lecteur = new MultipartReader(request);

        System.out.println("[BINDING] " + method.getDeclaringClass().getSimpleName()
                + "." + method.getName() + " -> " + params.length + " parametre(s) a lier");

        afficherParametresRecus(lecteur);

        for (int i = 0; i < params.length; i++) {
            Class<?> type = params[i].getType();
            String nomDeclare = params[i].getName();
            String nom = nomParametre(nomDeclare, type);

            Object valeur = resoudreValeur(method, i, type, nomDeclare, nom, response, lecteur);

            arguments[i] = valeur;                       // <- la valeur placee DANS l'argument

            if (!isObjetDeRequete(type) && isNomJavaValide(nom)) {
                resultat.add(nom, valeur);
            }

            System.out.println("[BINDING]   arg" + i + " -> " + type.getSimpleName()
                    + " " + nom + " = " + afficher(valeur));
        }

        return resultat;
    }

    private static Object resoudreValeur(Method method, int index, Class<?> type,
                                         String nomDeclare, String nom,
                                         HttpServletResponse response,
                                         MultipartReader lecteur) throws BindingException {

        HttpServletRequest request = lecteur.getRequest();

        if (isObjetDeRequete(type)) return objetDeRequete(type, request, response);

        if (type == MySession.class) {
            return SessionManager.ouvrir(request);
        }

        if (type == MapFile.class) {
            return lecteur.lireFichier(nom, nom);
        }
        if (type == byte[].class) {
            return lecteur.lireOctets(nom, nom);
        }

        if (isSimpleType(type)) {
            return convertir(lireValeur(lecteur, nomDeclare, nom, type), type, nom);
        }

        if (isBean(type)) {
            return lierObjet(type, "", lecteur);
        }

        throw new BindingException(
                "le parametre " + (index + 1) + " de la methode "
                        + method.getName() + "() est de type " + type.getName()
                        + ", que le framework ne sait pas fabriquer. "
                        + "Types acceptes : types simples (String, int, boolean, ...), "
                        + "fichiers (MapFile, byte[]), "
                        + "session (MySession), "
                        + "objets de la requete (HttpServletRequest, HttpServletResponse, "
                        + "HttpSession, ServletContext) et objets metier ayant un "
                        + "constructeur vide (ex. Employe). "
                        + (type.isArray()
                                ? "Pour un fichier, utilise byte[] (et non un autre tableau)." 
                                : type.isInterface()
                                ? "Les interfaces ne sont pas supportees." : ""));
    }


    private static Object lierObjet(Class<?> type, String prefixe, MultipartReader lecteur)
            throws BindingException {

        Constructor<?> constructeur = constructeurVide(type);

        Object objet;
        try {
            objet = constructeur.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new BindingException("impossible d'instancier " + type.getName()
                    + " pour le binding. Son constructeur vide doit etre public.", e);
        }

        int nombreChamps = 0;

        // On boucle sur les CHAMPS de la classe (le "boucler" du cahier des charges).
        for (Field champ : type.getDeclaredFields()) {

            // static : partage par toute la classe, final : impossible a changer.
            // isSynthetic : les champs internes du compilateur (this$0...).
            if (Modifier.isStatic(champ.getModifiers())
                    || Modifier.isFinal(champ.getModifiers())
                    || champ.isSynthetic()) {
                continue;
            }

            String nomChamp = champ.getName();
            String nomRecherche = prefixe.isEmpty() ? nomChamp : prefixe + "." + nomChamp;

            Class<?> typeChamp = champ.getType();
            String brut = lireValeurObjet(lecteur, nomRecherche, nomChamp);

            Object valeur;

            if (typeChamp == MapFile.class) {
                valeur = lecteur.lireFichier(nomRecherche, nomChamp);
            } else if (typeChamp == byte[].class) {
                valeur = lecteur.lireOctets(nomRecherche, nomChamp);
            } else if (typeChamp == MySession.class) {
                throw new BindingException("le champ '" + nomRecherche + "' de "
                        + type.getName() + " est de type MySession : la session "
                        + "n'est pas une donnee de formulaire. Declarez-la en "
                        + "parametre de la methode d'action "
                        + "(ex. save(Employe employe, MySession session)).");
            } else if (isBean(typeChamp)) {
                valeur = lierObjet(typeChamp, nomRecherche, lecteur);
            } else if (isTypeSimpleLiable(typeChamp)) {
                valeur = convertir(brut, typeChamp, nomRecherche);
            } else if (brut == null) {
                valeur = valeurInitiale(champ);
                System.out.println("[BINDING]   " + nomRecherche + " : type "
                        + typeChamp.getSimpleName() + " non lie, champ laisse tel quel");
            } else {
                throw new BindingException("le champ '" + nomRecherche + "' de "
                        + type.getName() + " est de type " + typeChamp.getName()
                        + ", type que le binding ne sait pas convertir.");
            }

            ecrireChamp(objet, champ, valeur, type);
            nombreChamps++;
            System.out.println("[BINDING]     " + nomRecherche + " (" + nomChamp + ") = "
                    + afficher(valeur));
        }

        System.out.println("[BINDING]   objet " + type.getSimpleName() + " instancie : "
                + nombreChamps + " champ(s) traite(s)"
                + (prefixe.isEmpty() ? "" : " (prefixe '" + prefixe + ".')"));
        return objet;
    }


    private static String lireValeurObjet(MultipartReader lecteur, String nomComplet, String nomChamp)
            throws BindingException {
        String valeur = lecteur.lireTexte(nomComplet);
        if (valeur == null && !nomComplet.equals(nomChamp)) {
            valeur = lecteur.lireTexte(nomChamp);
        }
        return valeur;
    }

    private static void ecrireChamp(Object objet, Field champ, Object valeur, Class<?> typeObjet)
            throws BindingException {
        try {
            Method setter = findSetter(typeObjet, champ);
            if (setter != null) {
                setter.invoke(objet, valeur);
                return;
            }
            champ.setAccessible(true);      // le champ est private : on ouvre l'acces
            champ.set(objet, valeur);
        } catch (InvocationTargetException e) {
            throw new BindingException("le setter de '" + champ.getName() + "' de "
                    + typeObjet.getName() + " a refuse la valeur \"" + afficher(valeur) + "\" : "
                    + (e.getCause() == null ? "raison inconnue" : e.getCause().getMessage()), e);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new BindingException("impossible d'ecrire le champ '" + champ.getName()
                    + "' de " + typeObjet.getName() + " (valeur : " + afficher(valeur) + ")", e);
        }
    }

    private static Method findSetter(Class<?> type, Field champ) {
        String nomSetter = "set"
                + Character.toUpperCase(champ.getName().charAt(0))
                + champ.getName().substring(1);
        try {
            Method setter = type.getDeclaredMethod(nomSetter, champ.getType());
            setter.setAccessible(true);
            return setter;
        } catch (NoSuchMethodException e) {
            return null;                    // pas de setter : on ecrit dans le champ
        }
    }

    private static Constructor<?> constructeurVide(Class<?> type) throws BindingException {
        try {
            Constructor<?> c = type.getDeclaredConstructor();
            c.setAccessible(true);
            return c;
        } catch (NoSuchMethodException e) {
            throw new BindingException("le framework ne sait pas fabriquer un objet "
                    + type.getName() + " : cette classe n'a pas de constructeur vide. "
                    + "Ajoute public " + type.getSimpleName() + "() {}.");
        }
    }

    private static Object valeurInitiale(Field champ) throws BindingException {
        return valeurParDefaut(champ.getType(), champ.getName());
    }

    public static boolean isBean(Class<?> type) {
        if (type == null) return false;
        if (type.isPrimitive() || type.isArray() || type.isEnum()) return false;
        // SPRINT 9 : MySession n'est pas un objet metier a remplir champ par
        // champ, c'est la session de la requete (c'est le binding qui la fabrique).
        if (type == MySession.class) return false;
        if (isSimpleType(type) || isObjetDeRequete(type)) return false;
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) return false;
        try {
            type.getDeclaredConstructor();
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /** HttpServletRequest, HttpServletResponse, HttpSession, ServletContext. */
    public static boolean isObjetDeRequete(Class<?> type) {
        return type == HttpServletRequest.class
                || type == HttpServletResponse.class
                || type == HttpSession.class
                || type == ServletContext.class;
    }

    private static Object objetDeRequete(Class<?> type, HttpServletRequest request,
                                          HttpServletResponse response) {
        if (type == HttpServletRequest.class)  return request;
        if (type == HttpServletResponse.class) return response;
        if (type == HttpSession.class)        return request.getSession();
        return request.getServletContext();  // ServletContext
    }

    /** isSimpleType + HttpSession/ServletContext : ce que l'on sait mettre dans un champ. */
    private static boolean isTypeSimpleLiable(Class<?> type) {
        return isSimpleType(type)
                || type == HttpServletRequest.class
                || type == HttpServletResponse.class
                || type == HttpSession.class
                || type == ServletContext.class;
    }

    private static String lireValeur(MultipartReader lecteur, String nomDeclare,
                                     String nom, Class<?> type) throws BindingException {
        String brut = lecteur.lireTexte(nom);
        if (brut != null) return brut;

        if (nomDeclare == null || estNomGenerique(nomDeclare)) {
            for (String alias : aliasParType(type)) {
                String valeur = lecteur.lireTexte(alias);
                if (valeur != null) {
                    System.out.println("[BINDING]   (nom reel du parametre inconnu : "
                            + nomDeclare + ") -> champ '" + alias + "' utilise");
                    return valeur;
                }
            }
        }
        return null;
    }

    private static Object convertir(String brut, Class<?> type, String nom) throws BindingException {
        boolean absent = (brut == null);

        if (absent || (brut.isEmpty() && type != String.class)) {
            return valeurParDefaut(type, nom);
        }

        try {
            if (type == String.class) {
                return brut;
            } else if (type == char.class || type == Character.class) {
                if (brut.length() != 1) throw new NumberFormatException("un seul caractere attendu, recu '" + brut + "'");
                return brut.charAt(0);

            } else if (type == boolean.class || type == Boolean.class) {
                return convertirBooleen(brut);

            } else if (type == int.class || type == Integer.class)          return Integer.parseInt(brut.trim());
            else if (type == long.class || type == Long.class)             return Long.parseLong(brut.trim());
            else if (type == short.class || type == Short.class)           return Short.parseShort(brut.trim());
            else if (type == byte.class || type == Byte.class)             return Byte.parseByte(brut.trim());
            else if (type == float.class || type == Float.class)           return Float.parseFloat(brut.trim());
            else if (type == double.class || type == Double.class)         return Double.parseDouble(brut.trim());
            else if (type == BigInteger.class)                             return new BigInteger(brut.trim());
            else if (type == BigDecimal.class)                             return new BigDecimal(brut.trim());
            else if (type.isEnum())                                         return convertirEnum(type, brut);

        } catch (NumberFormatException | ArithmeticException e) {
            throw new BindingException("la valeur \"" + brut + "\" recue pour le parametre '"
                    + nom + "' n'est pas convertible en " + type.getSimpleName() + ".", e);
        }

        throw new BindingException("type non gere par le binding : " + type.getName());
    }

    /** Les cases a cocher envoient "on" quand elles sont cochees. */
    private static Boolean convertirBooleen(String brut) {
        String v = brut.trim().toLowerCase(Locale.ROOT);
        if (v.equals("true") || v.equals("on") || v.equals("oui") || v.equals("yes") || v.equals("1")) return Boolean.TRUE;
        if (v.equals("false") || v.equals("off") || v.equals("non") || v.equals("no") || v.equals("0")) return Boolean.FALSE;
        throw new NumberFormatException("'" + brut + "' n'est pas un booleen");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object convertirEnum(Class<?> type, String brut) {
        String v = brut.trim();
        try {
            return Enum.valueOf((Class<Enum>) type.asSubclass(Enum.class), v);
        } catch (IllegalArgumentException e) {
            for (Object c : type.getEnumConstants()) {
                if (((Enum<?>) c).name().equalsIgnoreCase(v)) return c;
            }
            throw e;
        }
    }

    /** 2. "par defaut null" : null pour un objet, la valeur neutre pour un primitif. */
    private static Object valeurParDefaut(Class<?> type, String nom) throws BindingException {
        System.out.println("[BINDING]   aucune valeur pour '" + nom + "' -> valeur par defaut");
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return Boolean.FALSE;
        if (type == char.class)    return '\0';
        if (type == int.class)     return 0;
        if (type == long.class)    return 0L;
        if (type == short.class)   return (short) 0;
        if (type == byte.class)    return (byte) 0;
        if (type == float.class)   return 0f;
        if (type == double.class)  return 0d;
        throw new BindingException("type primitif non gere : " + type.getName());
    }

    private static String nomParametre(String nomDeclare, Class<?> type) {
        if (nomDeclare != null && !estNomGenerique(nomDeclare)) return nomDeclare;
        return type.getSimpleName().toLowerCase(Locale.ROOT);
    }

    private static boolean estNomGenerique(String nom) {
        return nom.isEmpty() || nom.startsWith("arg") && nom.substring(3).chars().allMatch(Character::isDigit);
    }

    private static String[] aliasParType(Class<?> type) {
        if (type == String.class) return new String[]{"nom", "name"};
        if (type == boolean.class || type == Boolean.class) return new String[]{"actif"};
        return VIDE;
    }

    private static boolean isNomJavaValide(String nom) {
        if (nom == null || nom.isEmpty()) return false;
        if (!Character.isJavaIdentifierStart(nom.charAt(0))) return false;
        for (int i = 1; i < nom.length(); i++) {
            if (!Character.isJavaIdentifierPart(nom.charAt(i))) return false;
        }
        return true;
    }

    public static boolean isSimpleType(Class<?> type) {
        return type.isPrimitive()
                || type == String.class
                || type == Character.class
                || type == Boolean.class
                || Number.class.isAssignableFrom(type)
                || type.isEnum();
    }

    public static boolean isBindable(Class<?> type) {
        return isSimpleType(type)
                || isObjetDeRequete(type)
                || isBean(type)
                // SPRINT 8 : un fichier (MapFile) ou son contenu nu (byte[])
                || type == MapFile.class
                || type == byte[].class
                // SPRINT 9 : la session de l'utilisateur
                || type == MySession.class;
    }

    public static void afficherParametresRecus(HttpServletRequest request) {
        afficherParametresRecus(new MultipartReader(request));
    }

    public static void afficherParametresRecus(MultipartReader lecteur) {
        // SPRINT 8 : en multipart, getParameterMap() est vide : c'est le
        // lecteur qui liste les champs texte ET les fichiers.
        if (lecteur.estMultipart()) {
            lecteur.afficherRecu();
            return;
        }
        Map<String, String[]> tous = lecteur.getRequest().getParameterMap();
        if (tous == null || tous.isEmpty()) {
            System.out.println("[BINDING]   (aucun parametre dans la requete)");
            return;
        }
        Set<String> noms = new LinkedHashSet<>(tous.keySet());
        for (String nom : noms) {
            String[] valeurs = tous.get(nom);
            String aff = (valeurs != null && valeurs.length > 0) ? valeurs[0] : "";
            System.out.println("[BINDING]   requete: " + nom + " = \"" + aff + "\"");
        }
    }

    private static String afficher(Object valeur) {
        return afficher(valeur, 0);
    }

    private static String afficher(Object valeur, int profondeur) {
        if (valeur == null) return "null";
        if (valeur instanceof String) return "\"" + valeur + "\"";
        // SPRINT 8 : ne JAMAIS imprimer le contenu d'un fichier (illisible).
        if (valeur instanceof MapFile) {
            MapFile fichier = (MapFile) valeur;
            if (fichier.estVide()) return "MapFile(vide)";
            return "MapFile(fileName=\"" + fichier.getFileName() + "\", "
                    + fichier.getTaille() + " octets)";
        }
        if (valeur instanceof byte[]) {
            return "byte[" + ((byte[]) valeur).length + " octets]";
        }
        // SPRINT 9 : la session s'affiche par son id et son nombre
        // d'attributs (jamais les valeurs : elles peuvent etre sensibles).
        if (valeur instanceof MySession) {
            MySession session = (MySession) valeur;
            return "MySession(id=" + session.getId() + ", "
                    + session.taille() + " attribut(s))";
        }
        // Sprint 7bis 
        
        if (profondeur < 2 && isBean(valeur.getClass())) {
            StringBuilder sb = new StringBuilder(valeur.getClass().getSimpleName()).append("{");
            String separator = "";
            for (Field champ : valeur.getClass().getDeclaredFields()) {
                if (Modifier.isStatic(champ.getModifiers()) || champ.isSynthetic()) continue;
                try {
                    champ.setAccessible(true);
                    sb.append(separator).append(champ.getName()).append("=")
                            .append(afficher(champ.get(valeur), profondeur + 1));
                } catch (IllegalAccessException e) {
                    sb.append(separator).append(champ.getName()).append("=?");
                }
                separator = ", ";
            }
            return sb.append("}").toString();
        }
        return String.valueOf(valeur);
    }
}