package Entity;

import java.util.Date;

public class Demande {
    private int id_demande;
    private int id_rendezvous;
    private int don_id;
    private Date date;
    private String description;
    private String statut;
    private String titre;
    private int directeurCampagne;

    public Demande() {
    }

    public Demande(int id_demande, int id_rendezvous, int don_id, Date date, String description, String statut, String titre, int directeurCampagne) {
        this.id_demande = id_demande;
        this.id_rendezvous = id_rendezvous;
        this.don_id = don_id;
        this.date = date;
        this.description = description;
        this.statut = statut;
        this.titre = titre;
        this.directeurCampagne = directeurCampagne;
    }
    public Demande(int id_demande, String description) {
        this.id_demande = id_demande;

    }

    public Demande(int don_id, Date date, String description,String titre, int directeurCampagne) {
        this.don_id = don_id;
        this.date = date;
        this.description = description;
        this.statut = "ENCOURS DE TRAITMENT";
        this.titre = titre;
        this.directeurCampagne = directeurCampagne;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public int getId_demande() {
        return id_demande;
    }

    public void setId_demande(int id_demande) {
        this.id_demande = id_demande;
    }

    public int getId_rendezvous() {
        return id_rendezvous;
    }

    public void setId_rendezvous(int id_rendezvous) {
        this.id_rendezvous = id_rendezvous;
    }

    public int getDon_id() {
        return don_id;
    }

    public void setDon_id(int don_id) {
        this.don_id = don_id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getDirecteurCampagne() {
        return directeurCampagne;
    }

    public void setDirecteurCampagne(int directeurCampagne) {
        this.directeurCampagne = directeurCampagne;
    }

    @Override
    public String toString() {
        return "Demande{" +
                "id_demande=" + id_demande +
                ", id_rendezvous=" + id_rendezvous +
                ", don_id=" + don_id +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", statut='" + statut + '\'' +
                ", directeurCampagne=" + directeurCampagne +
                '}';
    }
}
