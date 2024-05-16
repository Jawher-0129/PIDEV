package Entity;

import java.sql.Timestamp;
import java.util.Date;

public class RendezVous {
    private int id_rendezvous;
    private Timestamp date;
    private String lieu;
    private String objective;
    private  int demande;

    public RendezVous(Timestamp date, String lieu, String objective, int demande) {
        this.date = date;
        this.lieu = lieu;
        this.objective = objective;
        this.demande = demande;
    }

    public RendezVous(int id_rendezvous, Timestamp date, String lieu, String objective, int demande) {
        this.id_rendezvous = id_rendezvous;
        this.date = date;
        this.lieu = lieu;
        this.objective = objective;
        this.demande = demande;
    }

    @Override
    public String toString() {
        return "RendezVous{" +
                "id_rendezvous=" + id_rendezvous +
                ", date=" + date +
                ", lieu='" + lieu + '\'' +
                ", objective='" + objective + '\'' +
                ", demande=" + demande +
                '}';
    }

    public int getId_rendezvous() {
        return id_rendezvous;
    }

    public void setId_rendezvous(int id_rendezvous) {
        this.id_rendezvous = id_rendezvous;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public int getDemande() {
        return demande;
    }

    public void setDemande(int demande) {
        this.demande = demande;
    }
}
