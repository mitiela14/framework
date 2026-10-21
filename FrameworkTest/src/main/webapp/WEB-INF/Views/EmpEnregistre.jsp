<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Sprint 7 - Binding : employe enregistre</title>
</head>
<body>

    <h1>Employe enregistre</h1>

    <%--
        Les valeurs affichees ici sont celles que le framework a LIEES.
        Elles sont disponibles de deux facons :
          - parce que le controleur les a mises dans le ModelView,
          - parce que le framework les met automatiquement comme attributs
            de la requete (request.getAttribute("nom")).
    --%>
    <%
        Object nom = request.getAttribute("nom");
        Object age = request.getAttribute("age");
    %>

    <table border="1" cellpadding="6">
        <tr><th>Nom</th><td><%= nom == null ? "(null : aucune valeur recue)" : nom %></td></tr>
        <tr><th>Age</th><td><%= age == null ? "(null : aucune valeur recue)" : age %></td></tr>
    </table>

    <p><a href="<%= request.getContextPath() %>/emp/nouveau">Revenir au formulaire</a></p>

</body>
</html>