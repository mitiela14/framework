<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.entity.Employe" %>
<%@ page import="com.entity.Adresse" %>
<!DOCTYPE html>
<html>
<head>
    <title>Sprint 7bis - Binding d'objet</title>
</head>
<body>

    <%--
        L'objet Employe a ete fabrique et rempli par le framework.
        Il est lisible de deux facons :
          - request.getAttribute("employe") : pose automatiquement par le binding
          - Employe employe : fourni par le framework comme parametre de la methode
    --%>
    <%
        Employe employe = (Employe) request.getAttribute("employe");
        if (employe == null) employe = new Employe();
        Adresse adresse = employe.getAdresse();
    %>

    <h1>Employe construit par le framework (Sprint 7bis)</h1>

    <table border="1" cellpadding="6">
        <tr><th>Nom</th>
            <td><%= employe.getNom() == null ? "(null : aucun champ 'nom' recu)" : employe.getNom() %></td></tr>
        <tr><th>Age</th>
            <td><%= employe.getAge() %></td></tr>
        <tr><th>Actif</th>
            <td><%= employe.isActif() %></td></tr>
        <tr><th>Poste</th>
            <td><%= employe.getPoste() == null ? "(null)" : employe.getPoste() %></td></tr>
        <tr><th>Ville</th>
            <td><%= (adresse == null || adresse.getVille() == null) ? "(null)" : adresse.getVille() %></td></tr>
        <tr><th>Quartier</th>
            <td><%= (adresse == null || adresse.getQuartier() == null) ? "(null)" : adresse.getQuartier() %></td></tr>
    </table>

    <p>Objet complet : <code><%= employe.toString() %></code></p>

    <p><a href="<%= request.getContextPath() %>/emp/nouveau">Revenir au formulaire</a></p>

</body>
</html>