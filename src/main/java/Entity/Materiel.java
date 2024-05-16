package Entity;

public class Materiel {
    private int id;

    private String LibelleMateriel;

    private String Description;

    private int Disponibilite;

    private String ImageMateriel;


    private double Prix;
    private int id_categorie;

    private int id_user;


    public Materiel()
    {

    }

    public int getId_user() {
        return id_user;
    }

    public Materiel(String libelleMateriel, String description, int disponibilite, String imageMateriel, double prix, int id_categorie, int id_user) {
        LibelleMateriel = libelleMateriel;
        Description = description;
        Disponibilite = disponibilite;
        ImageMateriel = imageMateriel;
        Prix = prix;
        this.id_categorie = id_categorie;
        this.id_user = id_user;
    }

    public Materiel(int id, String libelleMateriel, String description, int disponibilite, String imageMateriel, double prix, int id_categorie) {
        this.id = id;
        LibelleMateriel = libelleMateriel;
        Description = description;
        Disponibilite = disponibilite;
        ImageMateriel = imageMateriel;
        Prix = prix;
        this.id_categorie = id_categorie;
    }


    public Materiel(int id, String libelleMateriel, String description, int disponibilite, String imageMateriel, double prix) {
        this.id = id;
        LibelleMateriel = libelleMateriel;
        Description = description;
        Disponibilite = disponibilite;
        ImageMateriel = imageMateriel;
        Prix = prix;
    }


    public Materiel(String libelleMateriel, String description, int disponibilite, String imageMateriel, double prix, int id_categorie) {
        LibelleMateriel = libelleMateriel;
        Description = description;
        Disponibilite = disponibilite;
        ImageMateriel = imageMateriel;
        Prix = prix;
        this.id_categorie = id_categorie;
    }



    public Materiel(String libelleMateriel, String description, int disponibilite, String imageMateriel, double prix) {
        LibelleMateriel = libelleMateriel;
        Description = description;
        Disponibilite = disponibilite;
        ImageMateriel = imageMateriel;
        Prix = prix;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLibelleMateriel() {
        return LibelleMateriel;
    }

    public void setLibelleMateriel(String libelleMateriel) {
        LibelleMateriel = libelleMateriel;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getDisponibilite() {
        return Disponibilite;
    }

    public void setDisponibilite(int disponibilite) {
        Disponibilite = disponibilite;
    }

    public String getImageMateriel() {
        return ImageMateriel;
    }

    public void setImageMateriel(String imageMateriel) {
        ImageMateriel = imageMateriel;
    }

    public double getPrix() {
        return Prix;
    }

    public void setPrix(double prix) {
        Prix = prix;
    }

    public int getId_categorie() {
        return id_categorie;
    }

    public void setId_categorie(int id_categorie) {
        this.id_categorie = id_categorie;
    }

    @Override
    public String toString() {
        return "Materiel{" +
                "id=" + id +
                ", LibelleMateriel='" + LibelleMateriel + '\'' +
                ", Description='" + Description + '\'' +
                ", Disponibilite=" + Disponibilite +
                ", ImageMateriel='" + ImageMateriel + '\'' +
                ", Prix=" + Prix +
                ", id_categorie=" + id_categorie +
                '}';
    }
}