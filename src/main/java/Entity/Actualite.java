package Entity;

public class Actualite {
    private int id_actualite;
    private String Titre ;
    private String Description;
    private String Type_pub_cible;
    private String Theme;

    private int id_user;


    public Actualite(String titre, String description, String type_pub_cible, String theme, int id_user) {
        Titre = titre;
        Description = description;
        Type_pub_cible = type_pub_cible;
        Theme = theme;
        this.id_user = id_user;
    }

    public int getId_user() {
        return id_user;
    }

    public Actualite(int id_actualite, String titre, String description, String type_pub_cible, String theme) {
        this.id_actualite = id_actualite;
        this.Titre = titre;
        this.Description = description;
        this.Type_pub_cible = type_pub_cible;
        this.Theme = theme;
    }

    public Actualite(String titre, String description, String type_pub_cible, String theme) {
        this.Titre = titre;
        this.Description = description;
        this.Type_pub_cible = type_pub_cible;
        this.Theme = theme;
    }

    public int getId_actualite() {
        return id_actualite;
    }

    public void setId_actualite(int id_actualite) {
        this.id_actualite = id_actualite;
    }

    public String getTitre() {
        return Titre;
    }

    public void setTitre(String titre) {
        this.Titre = titre;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public String getType_pub_cible() {
        return Type_pub_cible;
    }

    public void setType_pub_cible(String type_pub_cible) {
        this.Type_pub_cible = type_pub_cible;
    }

    public String getTheme() {
        return Theme;
    }



    public void setTheme(String theme) {
        this.Theme = theme;
    }

    @Override
    public String toString() {
        return "Actualite{" +
                "id_actualite=" + id_actualite +
                ", Titre='" + Titre + '\'' +
                ", Description='" + Description + '\'' +
                ", Type_pub_cible='" + Type_pub_cible + '\'' +
                ", Theme='" + Theme + '\'' +
                '}';
    }
}
