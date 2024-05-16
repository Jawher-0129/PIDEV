package Entity;

public class Chambre {
    private int numero;
    private int personnel;
    private int disponibilite;
    private int nombre_lits_total;
    private int nmbr_lits_disponible;

    public Chambre(int numero, int personnel, int disponibilite, int nombre_lits_total, int nmbr_lits_disponible) {
        this.numero = numero;
        this.personnel = personnel;
        this.disponibilite = disponibilite;
        this.nombre_lits_total = nombre_lits_total;
        this.nmbr_lits_disponible = nmbr_lits_disponible;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getPersonnel() {
        return personnel;
    }

    public void setPersonnel(int personnel) {
        this.personnel = personnel;
    }

    public int getDisponibilite() {
        return disponibilite;
    }

    public void setDisponibilite(int disponibilite) {
        this.disponibilite = disponibilite;
    }

    public int getNombre_lits_total() {
        return nombre_lits_total;
    }

    public void setNombre_lits_total(int nombre_lits_total) {
        this.nombre_lits_total = nombre_lits_total;
    }

    public int getNmbr_lits_disponible() {
        return nmbr_lits_disponible;
    }

    public void setNmbr_lits_disponible(int nmbr_lits_disponible) {
        this.nmbr_lits_disponible = nmbr_lits_disponible;
    }

    @Override
    public String toString() {
        return "Chambre{" +
                "numero=" + numero +
                ", personnel=" + personnel +
                ", disponibilite=" + disponibilite +
                ", nombre_lits_total=" + nombre_lits_total +
                ", nmbr_lits_disponible=" + nmbr_lits_disponible +
                '}';
    }
}

