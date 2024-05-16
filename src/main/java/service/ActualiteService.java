package service;



import Entity.Actualite;
import cnx.MyConnections;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActualiteService implements IService<Actualite> {
    private Connection cnx;
    private Statement ste;
    private PreparedStatement pst;
    public ActualiteService(){
        cnx= MyConnections.getInstance().getCnx();
    }
    public void add(Actualite a){
        String requete ="insert into Actualite(titre,description,type_pub_cible,theme,user_id) values(?,?,?,?,?)";
        try {
            pst=cnx.prepareStatement(requete);
            pst.setString(1,a.getTitre());
            pst.setString(2,a.getDescription());
            pst.setString(3, a.getType_pub_cible());
            pst.setString(4,a.getTheme());
            pst.setInt(5, a.getId_user());
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        String requete = "DELETE FROM actualite WHERE id_actualite = ?";
        try {
            pst = cnx.prepareStatement(requete);
            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Actualite actualite, int id) {
        String requete = "UPDATE actualite SET titre = ?, description = ?, type_pub_cible = ?, theme = ? WHERE id_actualite = ?";
        try {
            if (getById(id) != null) {
                pst = cnx.prepareStatement(requete);
                pst.setString(1, actualite.getTitre());
                pst.setString(2, actualite.getDescription());
                pst.setString(3, actualite.getType_pub_cible());
                pst.setString(4, actualite.getTheme());
                pst.setInt(5, id); // Setting id at the end
                pst.executeUpdate();
                System.out.println("Actualité mise à jour avec succès !");
            } else {
                System.out.println("Aucune actualité trouvée avec l'identifiant " + id + ". Impossible de mettre à jour.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Actualite> getAll() {
        String requete ="select * from actualite";
        List<Actualite> list= new ArrayList<>();
        try{
            ste= cnx.createStatement();
            ResultSet rs= ste.executeQuery(requete);
            while (rs.next()){
                list.add(new Actualite(rs.getInt("id_actualite"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getString("type_pub_cible"),
                        rs.getString("theme")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public Actualite getById(int id) {
        String requete = "SELECT * FROM actualite WHERE id_actualite = ?";
        try {
            pst = cnx.prepareStatement(requete);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new Actualite(
                        rs.getInt("id_actualite"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getString("type_pub_cible"),
                        rs.getString("theme")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
        /*public void addSTE(Actualite a){
        String requete ="insert into Actualite(titre,description,type_pub_cible,theme) values('"+a.getTitre()+"','"+a.getDescription()+"','"+a.getType_pub_cible()+"','"+a.getTheme()+"')";
        try {
            ste=cnx.createStatement();
            ste.executeUpdate(requete);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/

}
