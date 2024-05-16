package Entity;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Evenement {
    private int id_evenement;
    private String titre;
    private Date date;
    private int duree;
    private String lieu;
    private String objectif;
    private String image;
    private int id_actualite;
    private int id_user;

    public Evenement() {
    }

    public Evenement(String titre, Date date, int duree, String lieu, String objectif, String image, int id_actualite, int id_user) {
        this.titre = titre;
        this.date = date;
        this.duree = duree;
        this.lieu = lieu;
        this.objectif = objectif;
        this.image = image;
        this.id_actualite = id_actualite;
        this.id_user = id_user;
    }

    public int getId_user() {
        return id_user;
    }

    public Evenement(int id_evenement, String titre, Date date, int duree, String lieu, String objectif, String image, int id_actualite) {
        this.id_evenement = id_evenement;
        this.titre = titre;
        this.date = date;
        this.duree = duree;
        this.lieu = lieu;
        this.objectif = objectif;
        this.image = image;
        this.id_actualite = id_actualite;
    }

    public Evenement(String titre, Date date, int duree, String lieu, String objectif, String image, int id_actualite) {
        this.titre = titre;
        this.date = date;
        this.duree = duree;
        this.lieu = lieu;
        this.objectif = objectif;
        this.image = image;
        this.id_actualite = id_actualite;
    }
    public Evenement(String titre) {
        this.titre = titre;
    }

    public int getId_evenement() {
        return id_evenement;
    }

    public void setId_evenement(int id_evenement) {
        this.id_evenement = id_evenement;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getObjectif() {
        return objectif;
    }

    public void setObjectif(String objectif) {
        this.objectif = objectif;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId_actualite() {
        return id_actualite;
    }

    public void setId_actualite(int id_actualite) {
        this.id_actualite = id_actualite;
    }

    @Override
    public String toString() {
        return "Evenement{" +
                "id_evenement=" + id_evenement +
                ", titre='" + titre + '\'' +
                ", date=" + date +
                ", duree=" + duree +
                ", lieu='" + lieu + '\'' +
                ", objectif='" + objectif + '\'' +
                ", image='" + image + '\'' +
                ", id_actualite=" + id_actualite +
                '}';
    }


}
