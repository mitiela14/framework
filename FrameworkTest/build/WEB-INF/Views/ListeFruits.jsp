<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${titre}</title>
</head>
<body>
    <h1>${titre}</h1>

    <%
        List<?> fruits = (List<?>) request.getAttribute("fruits");
    %>

    <% if (fruits != null && !fruits.isEmpty()) { %>
        <p>${nombre} fruit(s) :</p>
        <ul>
            <% for (Object fruit : fruits) { %>
                <li><%= fruit %></li>
            <% } %>
        </ul>
    <% } else { %>
        <p>Le panier est vide.</p>
    <% } %>

    <p><a href="${pageContext.request.contextPath}/">Retour a l'accueil</a></p>
</body>
</html>
