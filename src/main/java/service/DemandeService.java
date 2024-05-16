package service;




import Entity.Demande;
import Entity.Don;
import cnx.MyConnections;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemandeService implements IService<Demande> {
    private Connection cnx;
    private Statement ste;
    private PreparedStatement pst;
    public DemandeService(){
        cnx= MyConnections.getInstance().getCnx();
    }
    public void add(Demande d) {
        String requete = "INSERT INTO demande (don_id, date, description, statut,titre, directeurCampagne) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement pst = cnx.prepareStatement(requete);
            pst.setInt(1, d.getDon_id());
            pst.setDate(2, new java.sql.Date(d.getDate().getTime()));
            pst.setString(3, d.getDescription());
            pst.setString(4, d.getStatut());
            pst.setString(5, d.getTitre());
            pst.setInt(6, d.getDirecteurCampagne());

            pst.executeUpdate();

            System.out.println("Demande ajoutée avec succès.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Demande demande,int id_demande) {
        String requete = "UPDATE demande SET don_id=?, date=?, description=?, statut=?, titre=?, directeurCampagne=? WHERE id_demande=?";
        try {
            PreparedStatement pst = cnx.prepareStatement(requete);
            pst.setInt(1, demande.getDon_id());
            pst.setDate(2, new java.sql.Date(demande.getDate().getTime()));
            pst.setString(3, demande.getDescription());
            pst.setString(4, demande.getStatut());
            pst.setString(5, demande.getTitre());
            pst.setInt(6, demande.getDirecteurCampagne());
            pst.setInt(7, id_demande); // spécifie l'identifiant de la demande à mettre à jour

            int rowsUpdated = pst.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("La demande a été mise à jour avec succès.");
            } else {
                System.out.println("Aucune demande n'a été mise à jour.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la demande : " + e.getMessage());
        }
    }
    public void delete(int id_demande) {
        String requete = "DELETE FROM demande WHERE id_demande=?";
        try {
            PreparedStatement pst = cnx.prepareStatement(requete);
            pst.setInt(1, id_demande);

            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("La demande a été supprimée avec succès.");
            } else {
                System.out.println("Aucune demande n'a été supprimée.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la demande : " + e.getMessage());
        }
    }
    public void Rejetee(int id_demande) {
        String requete = "UPDATE demande SET statut = 'REFUSEE' WHERE id_demande = ?";
        try {
            PreparedStatement pst = cnx.prepareStatement(requete);
            pst.setInt(1, id_demande);
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la demande : " + e.getMessage());
        }
    }
    public void Acceptee(int id_demande , int id_rendezvous) {
        String requete = "UPDATE demande SET statut = ? , id_rendezvous = ? WHERE id_demande = ?";
        try (PreparedStatement pst = cnx.prepareStatement(requete)) {
            pst.setString(1, "DEMANDE TRAITEE");
            pst.setInt(2, id_rendezvous);
            pst.setInt(3, id_demande);
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la demande : " + e.getMessage());
        }
    }


    public List<Demande> getAll() {
        String requete = "SELECT * FROM demande";
        List<Demande> list = new ArrayList<>();

        try {
            Statement statement = cnx.createStatement();
            ResultSet resultSet = statement.executeQuery(requete);

            while (resultSet.next()) {
                int id_demande = resultSet.getInt("id_demande");
                int id_rendezvous = resultSet.getInt("id_rendezvous");
                int don_id = resultSet.getInt("don_id");
                Date date = resultSet.getDate("date");
                String description = resultSet.getString("description");
                String statut = resultSet.getString("statut");
                String titre = resultSet.getString("titre");
                int directeurCampagne = resultSet.getInt("directeurCampagne");

                Demande demande = new Demande(id_demande, id_rendezvous, don_id, date, description, statut, titre, directeurCampagne);
                list.add(demande);
            }

            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des demandes : " + e.getMessage());
        }
    }
    public List<Demande> getDemandesUtilisateur(int idUtilisateur) {
        String requete = "SELECT * FROM demande WHERE directeurCampagne = ?";
        List<Demande> demandesUtilisateur = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = cnx.prepareStatement(requete);
            preparedStatement.setInt(1, idUtilisateur);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id_demande = resultSet.getInt("id_demande");
                int id_rendezvous = resultSet.getInt("id_rendezvous");
                int don_id = resultSet.getInt("don_id");
                Date date = resultSet.getDate("date");
                String description = resultSet.getString("description");
                String statut = resultSet.getString("statut");
                String titre = resultSet.getString("titre");
                int directeurCampagne = resultSet.getInt("directeurCampagne");

                Demande demande = new Demande(id_demande, id_rendezvous, don_id, date, description, statut, titre, directeurCampagne);
                demandesUtilisateur.add(demande);
            }

            return demandesUtilisateur;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des demandes de l'utilisateur : " + e.getMessage());
        }
    }

    public List<Demande> getAlltrie() {
        //String requete = "SELECT * FROM demande ORDER BY CASE WHEN id_rendezvous IS NULL THEN 0 ELSE 1 END, id_rendezvous";
        String requete = "SELECT d.*, COUNT(*) AS nombre_demandes FROM demande d LEFT JOIN user1 dc ON d.directeurCampagne = dc.id GROUP BY d.id_demande ORDER BY CASE WHEN d.id_rendezvous IS NULL THEN 0 ELSE 1 END, COUNT(*) DESC";      List<Demande> list = new ArrayList<>();

        try {
            Statement statement = cnx.createStatement();
            ResultSet resultSet = statement.executeQuery(requete);

            while (resultSet.next()) {
                int id_demande = resultSet.getInt("id_demande");
                int id_rendezvous = resultSet.getInt("id_rendezvous");
                int don_id = resultSet.getInt("don_id");
                Date date = resultSet.getDate("date");
                String description = resultSet.getString("description");
                String statut = resultSet.getString("statut");
                String titre = resultSet.getString("titre");
                int directeurCampagne = resultSet.getInt("directeurCampagne");

                Demande demande = new Demande(id_demande, id_rendezvous, don_id, date, description, statut, titre, directeurCampagne);
                list.add(demande);
            }

            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des demandes : " + e.getMessage());
        }
    }
    // Méthode pour récupérer un don par son ID
    public Don getDonById(int id) {
        String requete = "SELECT * FROM don WHERE id = ?";
        try {
            PreparedStatement pst = cnx.prepareStatement(requete);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                // Créez et retournez un nouvel objet Don avec les données récupérées de la base de données
                int idd = rs.getInt("id");
                String type = rs.getString("type");
                int montant = rs.getInt("montant");
                String date_remise = rs.getString("date_remise");
                int campagne_id = rs.getInt("campagne_id");
                Don don = new Don(idd, type, montant, date_remise, campagne_id);
                return don;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du don : " + e.getMessage());
        }
        return null; // Retourne null si aucun don correspondant n'est trouvé
    }

    public Map<Integer, String> getAllTypesWithIds() {
        Map<Integer, String> typesDeDon = new HashMap<>();
        String query = "SELECT id, type FROM don";
        try (PreparedStatement statement = cnx.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String type = resultSet.getString("type");
                typesDeDon.put(id, type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'exception selon les besoins de votre application
        }
        return typesDeDon;
    }



    public Demande getById(int id) {
        String requete = "SELECT * FROM demande WHERE id_demande = ?";

        try (PreparedStatement statement = cnx.prepareStatement(requete)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id_demande = resultSet.getInt("id_demande");
                    int id_rendezvous = resultSet.getInt("id_rendezvous");
                    int don_id = resultSet.getInt("don_id");
                    Date date = resultSet.getDate("date");
                    String description = resultSet.getString("description");
                    String statut = resultSet.getString("statut");
                    String titre = resultSet.getString("titre");
                    int directeurCampagne = resultSet.getInt("directeurCampagne");

                    // Retourne la demande trouvée
                    return new Demande(id_demande, id_rendezvous, don_id, date, description, statut, titre, directeurCampagne);
                } else {
                    // Aucune demande trouvée avec cet ID, retourne null
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de la demande avec l'ID " + id + " : " + e.getMessage());
        }
    }
    public String getEmailDirecteur(int idDirecteur) {
        String EmailDirecteur = null;
        String query = "SELECT email FROM user1 WHERE id = ?";
        // Connexion à la base de données
        try (PreparedStatement statement = cnx.prepareStatement(query)) {
            statement.setInt(1, idDirecteur);
            try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        EmailDirecteur = resultSet.getString("email");
                    }
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return EmailDirecteur;
    }
    public String getNumero(int idDirecteur) {
        String telephone = "";
        String query = "SELECT telephone FROM user1 WHERE id = ?";
        // Connexion à la base de données
        try (PreparedStatement statement = cnx.prepareStatement(query)) {
            statement.setInt(1, idDirecteur);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    telephone = resultSet.getString("telephone");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return telephone;
    }
    public String getNOMDirecteur(int idDirecteur) {
        String NomDirecteur = null;
        String PrenomDirecteur = null;

        String query = "SELECT nom,prenom FROM user1 WHERE id = ?";
        // Connexion à la base de données
        try (PreparedStatement statement = cnx.prepareStatement(query)) {
            statement.setInt(1, idDirecteur);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    NomDirecteur = resultSet.getString("nom");
                    PrenomDirecteur = resultSet.getString("prenom");

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return NomDirecteur+PrenomDirecteur;
    }



}



