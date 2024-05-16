package Entity;

public class Categorie {
    private int id;
    private String libelle;
    private int id_user;

    public Categorie(String libelle, int id_user) {
        this.libelle = libelle;
        this.id_user = id_user;
    }

    public Categorie(int id, String libelle) {
        this.id = id;
        this.libelle = libelle;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public Categorie(String libelle) {
        this.libelle = libelle;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String toString() {
        return "Categorie{id=" + this.id + ", libelle='" + this.libelle + "'}";
    }
}
