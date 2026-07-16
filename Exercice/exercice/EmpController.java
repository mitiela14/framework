package exercice;

@Controleur
public class EmpController {

    @UrlMapping("/emp/list")
    public void liste() { }

    @UrlMapping("/emp/new")
    public void create() { }

    public void interne() { }          // pas d'URL
}
