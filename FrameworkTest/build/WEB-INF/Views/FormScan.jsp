<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Sprint 8 - Upload de fichiers (multipart/form-data)</title>
</head>
<body>

    <h1><%= request.getAttribute("titre") %></h1>

    <p>
        Pour envoyer un fichier, le formulaire DOIT contenir
        <code>enctype="multipart/form-data"</code> et le champ
        <code>&lt;input type="file"&gt;</code>. Le <b>name</b> du champ doit
        correspondre au nom du <b>parametre</b> de la methode du controleur
        (ou au <b>champ</b> de l'objet Employe).
    </p>

    <hr>

    <%-- CAS 1 : fichiers en parametres directs --%>
    <h2>1. saveScan(String nom, MapFile rectoCIN, byte[] scanCIN)</h2>
    <form action="<%= request.getContextPath() %>/emp/enregistrer-scan"
          method="post" enctype="multipart/form-data">
        <p>
            <label>Nom :</label>
            <input type="text" name="nom" value="Rakoto">
        </p>
        <p>
            <label>Recto CIN (MapFile) :</label>
            <input type="file" name="rectoCIN">
        </p>
        <p>
            <label>Scan CIN (byte[]) :</label>
            <input type="file" name="scanCIN">
        </p>
        <p>
            <button type="submit">Envoyer (POST /emp/enregistrer-scan)</button>
        </p>
    </form>

    <hr>

    <%-- CAS 2 : les fichiers sont des CHAMPS de l'objet Employe --%>
    <h2>2. saveScanObjet(Employe employe) avec Employe.cinRecto / Employe.scanCIN</h2>
    <form action="<%= request.getContextPath() %>/emp/enregistrer-scan-objet"
          method="post" enctype="multipart/form-data">
        <p>
            <label>Nom :</label>
            <input type="text" name="nom" value="Rasoa">
        </p>
        <p>
            <label>Age :</label>
            <input type="number" name="age" value="30">
        </p>
        <p>
            <label>Poste :</label>
            <input type="text" name="poste" value="Developpeur">
        </p>
        <p>
            <label>cinRecto (MapFile) :</label>
            <input type="file" name="cinRecto">
        </p>
        <p>
            <label>scanCIN (byte[]) :</label>
            <input type="file" name="scanCIN">
        </p>
        <p>
            <button type="submit">Envoyer un OBJET + fichiers (POST /emp/enregistrer-scan-objet)</button>
        </p>
    </form>

    <hr>

    <%-- CAS 3 : meme binding en Web API (JSON) --%>
    <h2>3. saveScanJson(String nom, MapFile rectoCIN, byte[] scanCIN) -> JSON</h2>
    <form action="<%= request.getContextPath() %>/api/emp/enregistrer-scan"
          method="post" enctype="multipart/form-data">
        <p>
            <label>Nom :</label>
            <input type="text" name="nom" value="Jean">
        </p>
        <p>
            <label>Recto CIN :</label>
            <input type="file" name="rectoCIN">
        </p>
        <p>
            <label>Scan CIN :</label>
            <input type="file" name="scanCIN">
        </p>
        <p>
            <button type="submit">Envoyer en JSON (POST /api/emp/enregistrer-scan)</button>
        </p>
    </form>

    <hr>

    <p>
        Formulaire sans fichier (Sprint 7), pour verifier que rien n'est casse :
        <a href="<%= request.getContextPath() %>/emp/nouveau">/emp/nouveau</a>
    </p>

</body>
</html>
