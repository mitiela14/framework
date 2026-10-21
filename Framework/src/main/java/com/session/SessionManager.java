package com.session;

import javax.servlet.http.HttpServletRequest;


public final class SessionManager {


    private static final String CLE_SESSION_COURANTE = "com.session.courante";

    private SessionManager() {
    }

    public static MySession ouvrir(HttpServletRequest request) {
        MySession session = (MySession) request.getAttribute(CLE_SESSION_COURANTE);
        if (session == null) {
            session = new MySession(request);
            request.setAttribute(CLE_SESSION_COURANTE, session);
        }
        return session;
    }

    public static MySession courante(HttpServletRequest request) {
        return (MySession) request.getAttribute(CLE_SESSION_COURANTE);
    }


    public static void synchroniser(HttpServletRequest request) {
        MySession session = courante(request);
        if (session != null) {
            session.ecrireVersHttpSession();
        }
    }
}
