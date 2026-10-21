<%-- session="false" : cette vue n'a PAS besoin d'etre dans une session.
     Avec la valeur par defaut (true), Tomcat creerait une session a chaque
     affichage de la page, ce qui cacherait le vrai comportement du cadre
     (voir le test /session/invalider). --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <title>Sprint 9 - Session</title>
</head>
<body>

    <h1><%= request.getAttribute("titre") %></h1>

    <%-- Ce que le framework a recupere grace au parametre MySession --%>
    <p><b>Identifiant HttpSession (vu par le controleur) :</b> <%= request.getAttribute("idSession") %></p>
    <%-- null = la session n'existait pas encore au moment ou l'action a
         tourne : le framework la cree juste apres, en recopiant la Map de
         MySession dans HttpSession (cookie JSESSIONID cote navigateur). --%>
    <p><b>Premiere visite (session nouvelle) :</b> <%= request.getAttribute("nouvelle") %></p>
    <p><b>Session invalidee :</b> <%= request.getAttribute("invalidee") %></p>
    <p><b>Nombre d'attributs :</b> <%= request.getAttribute("taille") %></p>
    <% if (request.getAttribute("identiques") != null) { %>
        <p><b>Les deux parametres MySession sont la meme instance :</b>
           <%= request.getAttribute("identiques") %></p>
    <% } %>

    <h2>Contenu de la session</h2>
    <table border="1" cellpadding="6" cellspacing="0">
        <tr><th>Nom de l'attribut</th><th>Valeur</th></tr>
        <%
            Map contenu = (Map) request.getAttribute("contenu");
            if (contenu == null || contenu.isEmpty()) {
        %>
            <tr><td colspan="2"><i>(session vide)</i></td></tr>
        <%
            } else {
                for (Object cle : contenu.keySet()) {
        %>
            <tr><td><%= cle %></td><td><%= contenu.get(cle) %></td></tr>
        <%
                }
            }
        %>
    </table>

    <hr>

    <h2>Ajouter un attribut</h2>
    <form action="<%= request.getContextPath() %>/session/enregistrer" method="post">
        <p><label>Nom :</label> <input type="text" name="cle" value="utilisateur"></p>
        <p><label>Valeur :</label> <input type="text" name="valeur" value="Rakoto"></p>
        <p><button type="submit">Enregistrer dans la session</button></p>
    </form>

    <h2>Retirer un attribut</h2>
    <form action="<%= request.getContextPath() %>/session/supprimer" method="get">
        <p><label>Nom :</label> <input type="text" name="cle" value="utilisateur"></p>
        <p><button type="submit">Supprimer</button></p>
    </form>

    <h2>Autres actions</h2>
    <ul>
        <li><a href="<%= request.getContextPath() %>/session/afficher">/session/afficher</a> : compteur de visites</li>
        <li><a href="<%= request.getContextPath() %>/session/meme-instance">/session/meme-instance</a> : une seule MySession par requete</li>
        <li><a href="<%= request.getContextPath() %>/session/vider">/session/vider</a> : vider la session</li>
        <li><a href="<%= request.getContextPath() %>/session/invalider">/session/invalider</a> : detruire la session</li>
        <li><a href="<%= request.getContextPath() %>/api/session">/api/session</a> : contenu en JSON</li>
        <li><a href="<%= request.getContextPath() %>/api/session/invalider">/api/session/invalider</a> : invalider, en JSON</li>
    </ul>

</body>
</html>
