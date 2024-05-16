package service;



import Entity.RendezVous;
import cnx.MyConnections;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RendezVousService implements IService<RendezVous>{
    private Connection cnx;
    private Statement ste;
    private PreparedStatement pst;

    public RendezVousService() {
        cnx = MyConnections.getInstance().getCnx();
    }

    @Override
    public void add(RendezVous r) {
        String requete = "INSERT INTO rendez_vous (demande, date, lieu, objective) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement pst = cnx.prepareStatement(requete);
            pst.setInt(1, r.getDemande());
            pst.setTimestamp(2, r.getDate());
            pst.setString(3, r.getLieu());
            pst.setString(4, r.getObjective());
            pst.executeUpdate();
            System.out.println("RendezVous ajoutée avec succès.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id_rendezvous){
        String requete = "DELETE FROM rendez_vous WHERE id_rendezvous=?";
        try {
            PreparedStatement pst = cnx.prepareStatement(requete);
            pst.setInt(1, id_rendezvous);

            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("le rendez-Vous  a été supprimée avec succès.");
            } else {
                System.out.println("Aucune rendez_vous n'a été supprimée.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la rendez-vous : " + e.getMessage());
        }
    }


    public void update(RendezVous rv, int id_rendezvous) {
        String requete = "UPDATE rendez_vous SET demande=?, date=?, lieu=?, objective=? WHERE id_rendezvous=?";
        try {
            PreparedStatement pst = cnx.prepareStatement(requete);
            pst.setInt(1, rv.getDemande());
            pst.setDate(2, new java.sql.Date(rv.getDate().getTime()));
            pst.setString(3, rv.getLieu());
            pst.setString(4, rv.getObjective());
            pst.setInt(5, id_rendezvous);
            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Le rendez-vous a été mise à jour avec succès.");
            } else {
                System.out.println("Aucun rendez-vous n'a été mise à jour.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du rendez-vous : " + e.getMessage());
        }
    }


    @Override
    public List<RendezVous> getAll() {
        String requete = "SELECT id_rendezvous,demande, date, lieu, objective FROM rendez_vous";
        List<RendezVous> list = new ArrayList<>();

        try (Statement statement = cnx.createStatement();
             ResultSet resultSet = statement.executeQuery(requete)) {

            while (resultSet.next()) {
                int idRendezVous = resultSet.getInt("id_rendezvous");
                int demande = resultSet.getInt("demande");
                Timestamp date = resultSet.getTimestamp("date");
                String lieu = resultSet.getString("lieu");
                String objective = resultSet.getString("objective");

                RendezVous nouvelleR = new RendezVous(idRendezVous, date, lieu, objective, demande);
                list.add(nouvelleR);
            }

            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des demandes : " + e.getMessage());
        }
    }
    public List<RendezVous> getAllByUtilisateur(int idUtilisateur) {
        String requete = "SELECT r.id_rendezvous, r.demande, r.date, r.lieu, r.objective " +
                "FROM rendez_vous r " +
                "JOIN demande d ON r.demande = d.id_demande " +
                "WHERE d.directeurCampagne = ?";
        List<RendezVous> list = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = cnx.prepareStatement(requete);
            preparedStatement.setInt(1, idUtilisateur);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int idRendezVous = resultSet.getInt("id_rendezvous");
                int idDemande = resultSet.getInt("demande");
                Timestamp date = resultSet.getTimestamp("date");
                String lieu = resultSet.getString("lieu");
                String objective = resultSet.getString("objective");

                RendezVous rendezVous = new RendezVous(idRendezVous, date, lieu, objective, idDemande);
                list.add(rendezVous);
            }

            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des rendez-vous de l'utilisateur : " + e.getMessage());
        }
    }
    public String getDirecteurCampagne(int idDemande) {
        String nom = "";
        String prenom = "";

        String requete = "SELECT u.nom, u.prenom " +
                "FROM user1 u " +
                "JOIN demande d ON u.id = d.directeurCampagne " +
                "WHERE d.id_demande = ?";

        try {
            PreparedStatement preparedStatement = cnx.prepareStatement(requete);
            preparedStatement.setInt(1, idDemande);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                nom = resultSet.getString("nom");
                prenom = resultSet.getString("prenom");

            }

            return nom+prenom;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération dde nom et prenom de l'utilisateur : " + e.getMessage());
        }
    }

    @Override
    public RendezVous getById(int var1) {
        return null;
    }

    public Map<Integer, String> getAllTypesWithIds() {
        Map<Integer, String> typesDeDon = new HashMap<>();
        String query = "SELECT id_demande , titre FROM demande";
        try (PreparedStatement statement = cnx.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id_demande");
                String titre = resultSet.getString("titre");
                typesDeDon.put(id, titre);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'exception selon les besoins de votre application
        }
        return typesDeDon;
    }
    public int getIDR(int id_demande) {
        int id_rendezvous = 0;
        String query = "SELECT id_rendezvous FROM rendez_vous WHERE demande = ?";
        // Connexion à la base de données
        try (PreparedStatement statement = cnx.prepareStatement(query)) {
            statement.setInt(1, id_demande);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    id_rendezvous = resultSet.getInt("id_rendezvous");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id_rendezvous;
    }



}
