package service;

import Entity.Materiel;
import cnx.MyConnections;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;



import javax.swing.text.Document;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterielService implements IService<Materiel> {
    private Connection cnx;
    private Statement ste;
    private PreparedStatement pst;


    public MaterielService()
    {
        cnx= MyConnections.getInstance().getCnx();
    }

    public boolean rechercherCategorie(int idCategorie)
    {
        String requete="select * from categorie where id_categorie=?";
        try {
            pst=cnx.prepareStatement(requete);
            pst.setInt(1,idCategorie);
            return pst.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void add(Materiel materiel) {
        String requete="insert into materiel(id_categorie,libelle,description,disponibilite,image,prix,id_user_id) values" +
                "(?,?,?,?,?,?,?)";
        try {
            if(rechercherCategorie(materiel.getId_categorie())) {
                pst = cnx.prepareStatement(requete);
                pst.setInt(1, materiel.getId_categorie());
                pst.setString(2, materiel.getLibelleMateriel());
                pst.setString(3, materiel.getDescription());
                pst.setInt(4, materiel.getDisponibilite());
                pst.setString(5, materiel.getImageMateriel());
                pst.setDouble(6, materiel.getPrix());
                pst.setInt(7, materiel.getId_user());
                pst.executeUpdate();
                System.out.println("Ajout Materiel effectuee avec succees");
            }
            else
                System.out.println("Categorie n'existe pas");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String requete="delete from materiel where id_materiel=?";
        try {
            pst=cnx.prepareStatement(requete);
            pst.setInt(1,id);
            pst.executeUpdate();
            System.out.println("Suppression Materiel effectue");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Materiel materiel, int id) {

        String requete="update materiel set libelle=?,description=?,disponibilite=?,image=?,prix=?,id_categorie=? where id_materiel=?";
        try {
            pst=cnx.prepareStatement(requete);
            pst.setString(1,materiel.getLibelleMateriel());
            pst.setString(2,materiel.getDescription());
            pst.setInt(3,materiel.getDisponibilite());
            pst.setString(4,materiel.getImageMateriel());
            pst.setDouble(5,materiel.getPrix());
            pst.setInt(6,materiel.getId_categorie());
            pst.setInt(7,materiel.getId());

            this.pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public List<Materiel> getAll() {
        String requete="select * from materiel";
        List<Materiel> list=new ArrayList<>();
        try {
            ste=cnx.createStatement();
            ResultSet rs=ste.executeQuery(requete);
            while(rs.next())
            {
                list.add(new Materiel(rs.getInt("id_materiel"), rs.getString("libelle"),
                        rs.getString("description"), rs.getInt("disponibilite"),
                        rs.getString("image"), rs.getDouble("prix"),
                        rs.getInt("id_categorie")));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public Materiel getById(int id) {
        return null;
    }


    public ObservableList<Materiel> getMaterielByCategorie(int idCategorie) {
        String requete = "SELECT * FROM materiel WHERE id_categorie=?";
        ObservableList<Materiel> obList = FXCollections.observableArrayList();
        try (PreparedStatement ps = cnx.prepareStatement(requete)) {
            ps.setInt(1, idCategorie);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                obList.add(new Materiel(
                        rs.getInt("id_materiel"),
                        rs.getString("libelle"),
                        rs.getString("description"),
                        rs.getInt("disponibilite"),
                        rs.getString("image"),
                        rs.getDouble("prix")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return obList;
    }


    public List<Materiel> rechercherParLibelle(String libelle) {
        String requete = "SELECT * FROM materiel WHERE libelle LIKE ?";
        List<Materiel> list = new ArrayList<>();
        try {
            pst = cnx.prepareStatement(requete);
            pst.setString(1, "%" + libelle + "%"); // Utilisation du joker '%' pour trouver des correspondances partielles
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(new Materiel(
                        rs.getInt("id_materiel"),
                        rs.getString("libelle"),
                        rs.getString("description"),
                        rs.getInt("disponibilite"),
                        rs.getString("image"),
                        rs.getDouble("prix"),
                        rs.getInt("id_categorie")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static String chatGPT(String message) {
        String url = "https://api.openai.com/v1/chat/completions";
        String apiKey = "sk-proj-TNPMYPlVm7D2IUSB6ZE9T3BlbkFJLsHSobBUqgo0Z0Ki9OuU"; // API key goes here
        String model = "gpt-3.5-turbo"; // current model of chatgpt api

        try {
            // Create the HTTP POST request
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + apiKey);
            con.setRequestProperty("Content-Type", "application/json");

            // Build the request body
            String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + message + "\"}]}";
            con.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();

            // Get the response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // returns the extracted contents of the response.
            return extractContentFromResponse(response.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // This method extracts the response expected from chatgpt and returns it.
    public static String extractContentFromResponse(String response) {
        int startMarker = response.indexOf("content")+11; // Marker for where the content starts.
        int endMarker = response.indexOf("\"", startMarker); // Marker for where the content ends.
        return response.substring(startMarker, endMarker); // Returns the substring containing only the response.
    }


    public void envoyerSMS(String numeroDestinataire, String message) {
        // Remplacez les valeurs suivantes par vos propres informations Twilio
        String ACCOUNT_SID = "ACebbc4d10fe699654c6ac232f8017525b";
        String AUTH_TOKEN = "1e5dbe62af4f7528fb67b3ecb4783108";
        String TWILIO_NUMERO = "+13342039180";

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message twilioMessage = Message.creator(
                        new PhoneNumber(numeroDestinataire),
                        new PhoneNumber(TWILIO_NUMERO),
                        message)
                .create();

        System.out.println("SMS envoyé avec succès avec l'ID : " + twilioMessage.getSid());
    }






}
