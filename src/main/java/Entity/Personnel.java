package Entity;

public class Personnel {
    private int id_personnel;
    private  String nom;
    private  String prenom_personnel;
    private int disponibilite;
    private  String role;
    private int experience;
    private String image;
    private  int rating;
    private int user_id_id;




    public Personnel(int id_personnel, String nom, String prenom_personnel, int disponibilite, String role, int experience, String image, int rating, int user_id_id) {
        this.id_personnel = id_personnel;
        this.nom = nom;
        this.prenom_personnel = prenom_personnel;
        this.disponibilite = disponibilite;
        this.role = role;
        this.experience = experience;
        this.image = image;
        this.rating = rating;
        this.user_id_id = user_id_id;
    }

    public Personnel(String nom, String prenom_personnel, int disponibilite, String role, int experience, String image, int rating, int user_id_id) {
        this.nom = nom;
        this.prenom_personnel = prenom_personnel;
        this.disponibilite = disponibilite;
        this.role = role;
        this.experience = experience;
        this.image = image;
        this.rating = rating;
        this.user_id_id = user_id_id;
    }

    public int getId_personnel() {
        return id_personnel;
    }

    public void setId_personnel(int id_personnel) {
        this.id_personnel = id_personnel;
    }

    public  String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public  String getPrenom_personnel() {
        return prenom_personnel;
    }

    public void setPrenom_personnel(String prenom_personnel) {
        this.prenom_personnel = prenom_personnel;
    }

    public int getDisponibilite() {
        return disponibilite;
    }

    public void setDisponibilite(int disponibilite) {
        this.disponibilite = disponibilite;
    }

    public  String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public  int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getUser_id_id() {
        return user_id_id;
    }

    public void setUser_id_id(int user_id_id) {
        this.user_id_id = user_id_id;
    }


    public Personnel(String nom, String prenom_personnel, String role, String image, int rating) {
        this.nom = nom;
        this.prenom_personnel = prenom_personnel;
        this.role = role;
        this.image = image;
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Personnel{" +
                "id_personnel=" + id_personnel +
                ", nom='" + nom + '\'' +
                ", prenom_personnel='" + prenom_personnel + '\'' +
                ", disponibilite=" + disponibilite +
                ", role='" + role + '\'' +
                ", experience=" + experience +
                ", image='" + image + '\'' +
                ", rating=" + rating +
                ", user_id_id=" + user_id_id +
                '}';
    }
}

