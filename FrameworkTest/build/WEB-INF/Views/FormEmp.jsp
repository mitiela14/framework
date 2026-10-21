<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Sprint 7 - Binding : formulaire d'ajout</title>
</head>
<body>

    <h1><%= request.getAttribute("titre") %></h1>

    <!-- <p>
        Ce formulaire envoie des donnees vers le controleur.
        Le bouton <b>submit</b> declenche une requete <b>POST</b> vers l'URL
        <code>/emp/enregistrer</code> : c'est le <b>FrontControllerServlet</b> qui la recoit,
        qui verifie la route (URL + methode), qui fait le <b>binding</b> des parametres
        de <code>save(String nom, int age, boolean actif)</code>, puis qui invoque la methode.
    </p> -->

    <%-- Le nom des champs (name="...") DOIT correspondre aux noms des parametres --%>
    <form action="<%= request.getContextPath() %>/emp/enregistrer" method="post">
        <p>
            <label>Nom :</label>
            <input type="text" name="nom" value="Rakoto">
        </p>
        <p>
            <label>Age :</label>
            <input type="number" name="age" value="25">
        </p>
        <p>
            <button type="submit">Envoyer (POST /emp/enregistrer)</button>
        </p>
    </form>

    <hr>

    <h2>Binding d'objet (Sprint 7bis)</h2>

    <%--
        La methode est save(Employe employe) : le framework cree l'objet
        et le remplit. Les noms de champs sont donc ceux des CHAMPS de la
        classe Employe ; le sous-objet Adresse se prefiXE par "adresse.".
    --%>
    <form action="<%= request.getContextPath() %>/emp/enregistrer-objet" method="post">
        <p>
            <label>Nom :</label>
            <input type="text" name="nom" value="Rakoto">
        </p>
        <p>
            <label>Age :</label>
            <input type="number" name="age" value="25">
        </p>
        <p>
            <label>Actif :</label>
            <input type="checkbox" name="actif" value="true" checked>
        </p>
        <p>
            <label>Poste :</label>
            <input type="text" name="poste" value="Developpeur">
        </p>
        <p>
            <label>Ville :</label>
            <input type="text" name="adresse.ville" value="Antananarivo">
        </p>
        <p>
            <label>Quartier :</label>
            <input type="text" name="adresse.quartier" value="Analakely">
        </p>
        <p>
            <button type="submit">Envoyer un OBJET (POST /emp/enregistrer-objet)</button>
        </p>
    </form>

    <p>Le meme objet renvoye en JSON :</p>
    <form action="<%= request.getContextPath() %>/api/emp/enregistrer-objet" method="post">
        <input type="hidden" name="nom" value="Rasoa">
        <input type="hidden" name="age" value="30">
        <input type="hidden" name="poste" value="Testeur">
        <button type="submit">Envoyer en JSON</button>
    </form>

    <hr>

    <h2>Autres tests</h2>

    <p>1. Valeurs par defaut (formulaire vide) :</p>
    <form action="<%= request.getContextPath() %>/emp/enregistrer-vide" method="post">
        <button type="submit">Envoyer un formulaire vide</button>
    </form>

    <!-- <p>2. Meme chose mais en JSON (<code>/api/emp/enregistrer</code>) :</p> -->
    <form action="<%= request.getContextPath() %>/api/emp/enregistrer" method="post">
        <input type="text" name="nom" value="Jean">
        <input type="number" name="age" value="30">
        <button type="submit">Envoyer en JSON</button>
    </form>

    <hr>

    <h2>Sprint 8 - Upload de fichiers</h2>
    <p>
        Formulaire avec <code>&lt;input type="file"&gt;</code> (multipart) :
        <a href="<%= request.getContextPath() %>/emp/nouveau-scan">/emp/nouveau-scan</a>
    </p>

</body>
</html>