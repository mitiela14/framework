package com.controller;

import com.annotation.AnnotationController;
import com.annotation.JsonResponse;
import com.annotation.UrlMapping;
import com.model.ModelView;
import com.session.MySession;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;


@AnnotationController
public class SessionController {

    @UrlMapping(value = "/session/afficher", method = "GET")
    public ModelView afficher(MySession session) {

        Integer visites = (Integer) session.get("visites");
        visites = (visites == null) ? 1 : visites + 1;

        session.set("visites", visites);
        session.set("derniereVisite", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

        System.out.println("[CONTROLEUR] afficher() session=" + session);

        return vue(session, "Sprint 9 : la session utilisateur");
    }

    @UrlMapping(value = "/session/enregistrer", method = "POST")
    public ModelView enregistrer(MySession session, String cle, String valeur) {
        session.set(cle, valeur);
        System.out.println("[CONTROLEUR] enregistrer() " + cle + "=" + valeur
                + " -> session " + session);
        return vue(session, "Sprint 9 : attribut '" + cle + "' enregistre");
    }

    @UrlMapping(value = "/session/supprimer", method = "GET")
    public ModelView supprimer(MySession session, String cle) {
        Object ancienne = session.remove(cle);
        System.out.println("[CONTROLEUR] supprimer() " + cle
                + (ancienne == null ? " (cle inconnue)" : " (etait : " + ancienne + ")"));
        return vue(session, "Sprint 9 : attribut '" + cle + "' supprime");
    }

    @UrlMapping(value = "/session/vider", method = "GET")
    public ModelView vider(MySession session) {
        session.vider();
        System.out.println("[CONTROLEUR] vider() -> " + session);
        return vue(session, "Sprint 9 : session videe");
    }

    @UrlMapping(value = "/session/invalider", method = "GET")
    public ModelView invalider(MySession session) {
        session.invalider();
        System.out.println("[CONTROLEUR] invalider() -> " + session);
        return vue(session, "Sprint 9 : session invalidee");
    }


    @UrlMapping(value = "/session/meme-instance", method = "GET")
    public ModelView memeInstance(MySession premiere, MySession deuxieme) {
        boolean identiques = (premiere == deuxieme);
        System.out.println("[CONTROLEUR] memeInstance() premiere=" + System.identityHashCode(premiere)
                + " deuxieme=" + System.identityHashCode(deuxieme) + " -> " + identiques);

        ModelView mv = vue(premiere, "Sprint 9 : une seule MySession par requete");
        mv.addAttribute("identiques", identiques);
        return mv;
    }

    private ModelView vue(MySession session, String titre) {
        ModelView mv = new ModelView("Session");
        mv.addAttribute("titre", titre);
        mv.addAttribute("idSession", session.getId());
        mv.addAttribute("nouvelle", session.estNouvelle());
        mv.addAttribute("invalidee", session.estInvalidee());
        mv.addAttribute("taille", session.taille());
        mv.addAttribute("contenu", session.toutes());
        return mv;
    }

    @JsonResponse
    @UrlMapping(value = "/api/session", method = "GET")
    public Map<String, Object> etat(MySession session) {
        Map<String, Object> reponse = new LinkedHashMap<>();
        reponse.put("id", session.getId());
        reponse.put("nouvelle", session.estNouvelle());
        reponse.put("taille", session.taille());
        reponse.put("attributs", session.toutes());
        return reponse;
    }

    @JsonResponse
    @UrlMapping(value = "/api/session/invalider", method = "GET")
    public Map<String, Object> invaliderJson(MySession session) {
        session.set("avant", "avant invalider");
        session.invalider();
        Map<String, Object> reponse = new LinkedHashMap<>();
        reponse.put("invalidee", session.estInvalidee());
        reponse.put("attributs", session.toutes());
        return reponse;
    }
}
