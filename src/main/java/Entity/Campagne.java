package Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant l'entité Campagne.
 */
public class Campagne {
    private int id;
    private String titre;
    private String description;
    private String date_debut;
    private String date_fin;
    private String image; // New image attribute
    private Integer directeur_id;

    // Pour représenter la relation ManyToOne
    private List<Don> dons = new ArrayList<>();

    public List<Don> getDons() {
        return dons;
    }

    public void setDons(List<Don> dons) {
        this.dons = dons;
    }

    // Constructeur complet pour l'initialisation d'une campagne existante avec ID
    public Campagne(int id, String titre, String description, String date_debut, String date_fin, String image) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.image = image; // Set image
    }
    public Campagne(int id, String titre, String description, String date_debut, String date_fin, String image ,Integer directeur_id) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.image = image; // Set image
        this.directeur_id = directeur_id;
    }

    public Campagne(String date_debut, String date_fin, String description, Integer directeur_id, List<Don> dons, int id, String image, String titre) {
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.description = description;
        this.directeur_id = directeur_id;
        this.dons = dons;
        this.id = id;
        this.image = image;
        this.titre = titre;
    }
    public Campagne(String date_debut, String date_fin, String description, Integer directeur_id, String image, String titre) {
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.description = description;
        this.directeur_id = directeur_id;
        this.image = image;
        this.titre = titre;
    }

    public Integer getDirecteur_id() {
        return directeur_id;
    }

    public void setDirecteur_id(Integer directeur_id) {
        this.directeur_id = directeur_id;
    }

    // Constructeur pour l'initialisation d'une nouvelle campagne sans ID
    public Campagne(String titre, String description, String date_debut, String date_fin, String image) {
        this.titre = titre;
        this.description = description;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.image = image; // Set image
    }

    // Getters et setters pour chaque attribut
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate_debut() {
        return date_debut;
    }

    public void setDate_debut(String date_debut) {
        this.date_debut = date_debut;
    }

    public String getDate_fin() {
        return date_fin;
    }

    public void setDate_fin(String date_fin) {
        this.date_fin = date_fin;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    // Méthode toString pour représenter l'objet Campagne sous forme de chaîne de caractères
    @Override
    public String toString() {
        return "Campagne{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", date_debut='" + date_debut + '\'' +
                ", date_fin='" + date_fin + '\'' +
                ", image='" + image + '\'' + // Include image in toString
                '}';
    }
}