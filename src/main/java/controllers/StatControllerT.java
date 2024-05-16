package controllers;
import Entity.Demande;
import Entity.RendezVous;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import service.DemandeService;

public class StatControllerT implements Initializable {
    @FXML
    private PieChart pieCHARTTT;
    @FXML
    private PieChart pieCHARTTT1;
    @FXML
    private TextField text1;

    @FXML
    private TextField text2;
    @FXML
    private Button NotifirDirecteur;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        statt();
        statStaut();

      // retrieveDirecteursEnCours();
    }

    private DemandeService demandeService;

    public StatControllerT() {
        demandeService = new DemandeService();
    }
    void statt(){
        List<Demande> demandes = demandeService.getAll();

        // Comptage des demandes par directeur de campagne avec rendez-vous
        Map<Integer, Integer> statistiques = new HashMap<>();
        for (Demande demande : demandes) {
            int directeurId = demande.getDirecteurCampagne();
            int rendezvousId = demande.getId_rendezvous();

            // Vérifier si la demande a un rendez-vous associé
            if (rendezvousId != 0) {
                statistiques.put(directeurId, statistiques.getOrDefault(directeurId, 0) + 1);
            }
        }

        // Convertir les statistiques en une liste d'entrées
        List<Map.Entry<Integer, Integer>> statistiquesList = new ArrayList<>(statistiques.entrySet());

        // Créer une liste d'objets PieChart.Data pour le diagramme circulaire
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<Integer, Integer> entry : statistiquesList) {
            int directeurId = entry.getKey();
            String NOM = demandeService.getNOMDirecteur(directeurId);
            int nombreDemandes = entry.getValue();
            pieChartData.add(new PieChart.Data("Directeur " + NOM, nombreDemandes));
        }

        // Créer le PieChart
        pieCHARTTT.setData(pieChartData);
        pieCHARTTT.setTitle("Statistiques par Directeur de Campagne");

    }

    private void SendSMS() {
        String ACCOUNT_SID = "AC8bd6a0a5b22855c243dc60296543bf84";
        String AUTH_TOKEN = "c49c523215ae420d2cc398261c5a1b1b";

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        String twilioPhoneNumber = "+19149086373";
        String recipientPhoneNumber = "+21628749555"; // Remplacez par le numéro de téléphone du destinataire
        String messageBody = "Votre Rendez-vous est planifiee A cette Date :";
        Message message = Message.creator(
                        new PhoneNumber(recipientPhoneNumber),
                        new PhoneNumber(twilioPhoneNumber),
                        messageBody)
                .create();
    }
    void statStaut(){
        List<Demande> demandes = demandeService.getAll();

        // Comptage des demandes par statut
        Map<String, Integer> statistiques = new HashMap<>();
        for (Demande demande : demandes) {
            String statut = demande.getStatut();

            // Vérifier si la demande a un statut valide
            if (statut != null && !statut.isEmpty()) {
                statistiques.put(statut, statistiques.getOrDefault(statut, 0) + 1);
            }
        }

        // Convertir les statistiques en une liste d'entrées
        List<Map.Entry<String, Integer>> statistiquesList = new ArrayList<>(statistiques.entrySet());

        // Créer une liste d'objets PieChart.Data pour le diagramme circulaire
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : statistiquesList) {
            String statut = entry.getKey();
            int nombreDemandes = entry.getValue();
            pieChartData.add(new PieChart.Data(statut, nombreDemandes));
        }

        // Créer le PieChart
        pieCHARTTT1.setData(pieChartData);
        pieCHARTTT1.setTitle("Statistiques par Statut de Demande");
    }
    @FXML
    void retrieveDirecteursEnCours() {
        List<Demande> demandes = demandeService.getAll();
        List<Integer> directeursEnCours = new ArrayList<>();

        for (Demande demande : demandes) {
            String statut = demande.getStatut();
            int directeurId = demande.getDirecteurCampagne();

            if (statut != null && statut.equals("ENCOURS DE TRAITMENT")) {
                directeursEnCours.add(directeurId);
            }
        }
        String ACCOUNT_SID = "ACa428b26b3b5323e13afc19e4298f2cc6";
        String AUTH_TOKEN = "814fe9e4c855bca6b48963add3a35c35";

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        String twilioPhoneNumber = "+12562531431";
        for (int directeurId : directeursEnCours) {
            // Récupérez le numéro de téléphone du directeur de campagne à partir de la base de données
        //    String numeroTelephone = "+21620939384";
            String numeroTelephone = "+216" + demandeService.getNumero(directeurId);
            // Envoyez le SMS demandeService.getNumero(directeurId)
            Message message = Message.creator(
                    new PhoneNumber(numeroTelephone),
                    new PhoneNumber(twilioPhoneNumber),
                    "Message de votre application : Votre demande est en cours de traitement."
            ).create();

            // Vérifiez le statut de l'envoi du message
            if (message.getStatus() == Message.Status.FAILED) {
                System.out.println("Échec de l'envoi du SMS au directeur " + directeurId);
            } else {
                Notifications.create()
                        .title("Notification")
                        .text("SMS envoyé au directeur de campagne !")
                        .position(Pos.TOP_RIGHT)
                        .hideAfter(Duration.seconds(5))
                        .show();
                System.out.println("SMS envoyé au directeur " + directeurId);
            }
        }

        System.out.println("Directeurs de campagne en cours : " + directeursEnCours);
    }
    @FXML
    void openMap(ActionEvent event)
    {    Stage stage = new Stage();
        GluonMapExample mapExample = new GluonMapExample();
        mapExample.start(stage);

        Node node = (Node) event.getSource(); // Récupère le Node déclenchant l'événement
        Scene scene = node.getScene(); // Récupère la scène à partir du Node
        MapView mapView = (MapView) scene.lookup("#mapView"); // Remplacez "mapView" par l'ID de votre MapView
        MapPoint espritLocation = new MapPoint(36.89830009644064, 10.186927429895055);
        CustomCircleMarkerLayer markerLayer = new CustomCircleMarkerLayer(espritLocation);
        mapView.addLayer(markerLayer);
    }

}
