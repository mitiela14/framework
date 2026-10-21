<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.model.MapFile" %>
<%@ page import="com.entity.Employe" %>
<%!
    /** Apercu hexadecimal : on n'imprime jamais tout le fichier, juste le debut. */
    private String apercu(byte[] octets, int max) {
        if (octets == null) return "(aucun octet recu)";
        if (octets.length == 0) return "(fichier vide)";
        StringBuilder sb = new StringBuilder();
        int n = Math.min(octets.length, max);
        for (int i = 0; i < n; i++) {
            sb.append(String.format("%02X ", octets[i] & 0xFF));
        }
        if (octets.length > max) sb.append("...");
        return sb.toString();
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Sprint 8 - Fichiers recus par le binding</title>
</head>
<body>

    <%
        String nom = (String) request.getAttribute("nom");
        MapFile recto = (MapFile) request.getAttribute("rectoCIN");
        byte[] scan = (byte[]) request.getAttribute("scanCIN");
        Employe employe = (Employe) request.getAttribute("employe");
        String stockage = (String) request.getAttribute("stockage");
    %>

    <h1>Resultat du binding multipart (Sprint 8)</h1>

    <table border="1" cellpadding="6">
        <tr>
            <th>Nom</th>
            <td><%= nom == null ? "(null)" : nom %></td>
        </tr>

        <tr>
            <th>MapFile rectoCIN</th>
            <td>
                <% if (recto == null) { %>
                    (null : aucun fichier choisi pour "rectoCIN")
                <% } else { %>
                    fileName = <b><%= recto.getFileName() %></b><br>
                    contentType = <%= recto.getContentType() %><br>
                    taille = <%= recto.getTaille() %> octets<br>
                    extension = <%= recto.getExtension() %><br>
                    estVide() = <%= recto.estVide() %>
                <% } %>
            </td>
        </tr>

        <tr>
            <th>byte[] scanCIN</th>
            <td>
                <% if (scan == null) { %>
                    (null : aucun fichier choisi pour "scanCIN")
                <% } else { %>
                    longueur = <b><%= scan.length %></b> octets<br>
                    apercu = <code><%= apercu(scan, 16) %></code>
                <% } %>
            </td>
        </tr>

        <tr>
            <th>Apercu du recto</th>
            <td><code><%= recto == null ? "" : apercu(recto.getContenu(), 16) %></code></td>
        </tr>

        <tr>
            <th>Fichier ecrit sur le serveur</th>
            <td><%= stockage == null ? "(non demande)" : stockage %></td>
        </tr>

        <% if (employe != null) { %>
        <tr>
            <th>Objet Employe</th>
            <td><code><%= employe.toString() %></code></td>
        </tr>
        <% } %>
    </table>

    <p>
        Les fichiers stockes se trouvent dans le dossier
        <code>uploads/</code> de l'application (sous le repertoire web
        deploye par Tomcat).
    </p>

    <p><a href="<%= request.getContextPath() %>/emp/nouveau-scan">Revenir au formulaire</a></p>

</body>
</html>
