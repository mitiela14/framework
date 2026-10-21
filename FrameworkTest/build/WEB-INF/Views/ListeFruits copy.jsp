<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <title>Liste des Fruits</title>
</head>
<body>

    <h1>Fruits de la saison</h1>

    <%
        // Récupération de la liste depuis les attributs de requête
        List<?> fruits = (List<?>) request.getAttribute("Fruits");
    %>

    <% if (fruits != null && !fruits.isEmpty()) { %>
        <ul>
            <% for (Object fruitObj : fruits) { 
                   String fruit = fruitObj.toString();
            %>
                <li>🍓 <%= fruit %></li>
            <% } %>
        </ul>
    <% } else { %>
        <p>Le panier est vide. 🧺</p>
    <% } %>

</body>
</html>