package controllers;
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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import service.DemandeService;
import service.RendezVousService;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class RendezVousController {
    @FXML
    private TableView<RendezVous> rvtable;
    @FXML
    private Label demmm;


    @FXML
    private Button Ajouter;

    @FXML
    private DatePicker date;


    @FXML
    private TextField lieu;

    @FXML
    private TextArea objective;
    private RendezVousService rendezVousService;
    private DemandeService demandeService;
    @FXML
    private TableColumn<RendezVous,Timestamp> dater;

    @FXML
    private TableColumn<RendezVous,Integer> demander;
    @FXML
    private TableColumn<RendezVous,Integer> id_rendezvousr;

    @FXML
    private TableColumn<RendezVous,String> lieur;

    @FXML
    private TableColumn<RendezVous,String> objectiver;

    public RendezVousController() {
        rendezVousService = new RendezVousService();
        demandeService = new DemandeService();
    }
    private Map<Integer, String> typeDemandeMap = new HashMap<>();
    @FXML
    private ChoiceBox<String> heure;
    @FXML
    private TableColumn<RendezVous,Void> Action;

    @FXML
    private void initializeDemandeChoiceBox() {
        rvtable.setStyle("-fx-background-color: #ffffff; -fx-font-size: 14px;");

        demander.setStyle("-fx-text-fill: #000000;");
        lieur.setStyle("-fx-text-fill: #000000;");
        objectiver.setStyle("-fx-text-fill: #000000;");
// Appliquer les styles au formulaire

        /*add.setStyle("-fx-background-color: #FF5733; -fx-text-fill: #ffffff; -fx-font-size: 14px;");
        delete.setStyle("-fx-background-color: #FF5733; -fx-text-fill: #ffffff; -fx-font-size: 14px;");
        update.setStyle("-fx-background-color: #FF5733; -fx-text-fill: #ffffff; -fx-font-size: 14px;");*
        /
         */
        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute += 15) {
                String formattedHour = String.format("%02d", hour); // Ajoute un zéro en tête pour les heures < 10
                String formattedMinute = String.format("%02d", minute); // Ajoute un zéro en tête pour les minutes < 10
                heure.getItems().add(formattedHour + ":" + formattedMinute);
            }
        }

        // Définis une valeur par défaut, si nécessaire
        heure.setValue("00:00");


    }
    @FXML
    private int getKeyFromValue(Map<Integer, String> map, String value) {
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return -1; // Si la valeur n'est pas trouvée, retourne -1
    }
    @FXML
    private void initialize() {
        initializeTable();
        initializeDemandeChoiceBox();
        rvtable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Déplacer les données de la demande sélectionnée vers les champs de texte
                lieu.setText(newSelection.getLieu());
                objective.setText(newSelection.getObjective());


            } else {
                // Effacer les champs de texte si aucune demande n'est sélectionnée
                lieu.clear();
                objective.clear();

            }
        });
        Action.setCellFactory(param -> new TableCell<>() {
            private final Button updateR = new Button();
            private final Button deleteR = new Button();
            {
                updateR.setOnAction(event -> {
                    int index = getIndex();
                    if (index >= 0 && index < getTableView().getItems().size()) {
                       updateAPPO(index);
                    }
                });

                deleteR.setOnAction(event -> {
                    int index = getIndex();
                    if (index >= 0 && index < getTableView().getItems().size()) {
                        DeleteAPPo(index);
                    }
                });

                Glyph suppressionIcon = new Glyph("FontAwesome", FontAwesome.Glyph.TRASH);
                suppressionIcon.setColor(Color.RED);
                deleteR.setGraphic(suppressionIcon);
                deleteR.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");

                Glyph updateIcon = new Glyph("FontAwesome", FontAwesome.Glyph.REFRESH);
                updateIcon.setColor(Color.BLUE);
                updateR.setGraphic(updateIcon);
                updateR.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, updateR, deleteR);
                    setGraphic(buttons);
                }
            }
        });

    }
    private void initializeTable() {
        if (id_rendezvousr != null && demander != null && dater != null && lieur != null && objectiver != null ) {
            // Associer les propriétés aux colonnes de la TableView
            id_rendezvousr.setCellValueFactory(new PropertyValueFactory<>("id_rendezvous"));
            demander.setCellValueFactory(new PropertyValueFactory<>("demande"));
            dater.setCellValueFactory(new PropertyValueFactory<>("date"));
            lieur.setCellValueFactory(new PropertyValueFactory<>("lieu"));
            objectiver.setCellValueFactory(new PropertyValueFactory<>("objective"));

            // Charger les données dans la TableView
            loadRV();
        } else {
            System.err.println("Erreur : Les éléments de la TableView ou la TableView elle-même sont null.");
        }
    }

    private void loadRV() {
        try {
            List<RendezVous> rv = rendezVousService.getAll();
            ObservableList<RendezVous> observableRV = FXCollections.observableArrayList(rv);
            if (rvtable != null) {
                rvtable.setItems(observableRV);
            } else {
                System.err.println("Erreur : La TableView est null.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Gérer l'exception d'une manière appropriée
        }
    }
    @FXML
    protected void handleAddRendezVous() {
        /*String demandeTitre = demande.getValue();
        int demandeId = getKeyFromValue(typeDemandeMap, demandeTitre); */
        String idDemandeText = demmm.getText();
// Vérifiez si le texte contient uniquement des chiffres
        if (!idDemandeText.matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "ID de demande invalide", "L'ID de demande ne contient pas uniquement des chiffres.");
            return;
        }

// Si le texte ne contient que des chiffres, vous pouvez maintenant le convertir en entier en toute sécurité
        int demandeId = Integer.parseInt(idDemandeText);
        if (demandeId != -1) { // Vérifie si l'ID du don est valide
            String lieuText = lieu.getText(); // Récupère la description depuis la zone de texte
            String objectiveText = objective.getText();
            LocalDate localDate = date.getValue(); // Récupère la date depuis le DatePicker
            String selectedHour = heure.getValue();
            java.sql.Date dateSql = java.sql.Date.valueOf(localDate);
            if (localDate != null && selectedHour != null) {
                LocalTime localTime = LocalTime.parse(selectedHour); // Convertis l'heure sélectionnée en LocalTime
                LocalDateTime dateTime = LocalDateTime.of(localDate, localTime); // Combine la date et l'heure
                Timestamp timestamp = Timestamp.valueOf(dateTime); // Convertis en Timestamp

                RendezVous nouvelleR = new RendezVous(timestamp, lieuText, objectiveText,demandeId);
                // Ajoutez la nouvelle demande via le service
                rendezVousService.add(nouvelleR);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "RendezVous Ajout", "La RendezVous a été ajouté avec succès.");
                loadRV();
                SendSMS(nouvelleR);
                System.out.println(rendezVousService.getIDR(demandeId));
                demandeService.Acceptee(demandeId,rendezVousService.getIDR(demandeId));

            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Ajout de demande", "Veuillez sélectionner date");
            }

        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Ajout de demande", "Veuillez sélectionner un type de don valide.");
        }
    }
    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
    public void initData(int idDemande) {
        // Utilise l'ID de la demande pour effectuer des opérations dans l'interface RendezVous.fxml
        demmm.setText(String.valueOf(idDemande));
    }
    private void DeleteAPPo(int index) {
        RendezVous r = rvtable.getItems().get(index);
        int rendezVousid = r.getId_rendezvous();
        handleDeleteDemande(rendezVousid);
    }
   // @FXML
    protected void handleDeleteDemande(int id) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer le rendez-vous");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette rendez-vous ?");

        // Attendre la réponse de l'utilisateur
        Optional<ButtonType> result = confirmation.showAndWait();

        // Si l'utilisateur a confirmé la suppression
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Supprimer la demande à partir du service
            rendezVousService.delete(id);

            // Actualiser la TableView
            loadRV();}
        // Récupérer la demande sélectionnée dans la TableView
       /* RendezVous rvSelectionnee = rvtable.getSelectionModel().getSelectedItem();

        if (rvSelectionnee != null) {
            // Demander confirmation à l'utilisateur avant de supprimer la demande
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation");
            confirmation.setHeaderText("Supprimer le rendez-vous");
            confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette rendez-vous ?");

            // Attendre la réponse de l'utilisateur
            Optional<ButtonType> result = confirmation.showAndWait();

            // Si l'utilisateur a confirmé la suppression
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Supprimer la demande à partir du service
                rendezVousService.delete(rvSelectionnee.getId_rendezvous());

                // Actualiser la TableView
                loadRV();
            }
        } else {
            // Aucune demande sélectionnée, afficher un message d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un redez-vous à supprimer.");
            alert.showAndWait();
        }*/
    }

    private void updateAPPO(int index) {
        RendezVous r = rvtable.getItems().get(index);
        handleUpdateRendezV(r);
    }
    protected void handleUpdateRendezV(RendezVous RendezVous) {
        String lieuText = lieu.getText(); // Récupère la description depuis la zone de texte
        String objectiveText = objective.getText();
        LocalDate localDate = date.getValue(); // Récupère la date depuis le DatePicker
        String selectedHour = heure.getValue();
        LocalTime localTime = LocalTime.parse(selectedHour); // Convertis l'heure sélectionnée en LocalTime
        LocalDateTime dateTime = LocalDateTime.of(localDate, localTime); // Combine la date et l'heure
        Timestamp timestamp = Timestamp.valueOf(dateTime);
        RendezVous nouvelleR = new RendezVous(timestamp, lieuText, objectiveText,RendezVous.getDemande());
        rendezVousService.update(nouvelleR,RendezVous.getId_rendezvous());
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Demande mise à jour", "La demande a été mise à jour avec succès.");
        loadRV();
     /*  RendezVous rvSelectionnee = rvtable.getSelectionModel().getSelectedItem();
        if (rvSelectionnee != null) {
            // Récupérer les nouvelles valeurs des champs
            String lieuText = lieu.getText(); // Récupère la description depuis la zone de texte
            String objectiveText = objective.getText();
            LocalDate localDate = date.getValue(); // Récupère la date depuis le DatePicker
            String selectedHour = heure.getValue();
            LocalTime localTime = LocalTime.parse(selectedHour); // Convertis l'heure sélectionnée en LocalTime
            LocalDateTime dateTime = LocalDateTime.of(localDate, localTime); // Combine la date et l'heure
            Timestamp timestamp = Timestamp.valueOf(dateTime);
            RendezVous nouvelleR = new RendezVous(timestamp, lieuText, objectiveText,rvSelectionnee.getDemande());
            rendezVousService.update(nouvelleR,rvSelectionnee.getId_rendezvous());
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Demande mise à jour", "La demande a été mise à jour avec succès.");
            loadRV();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune demande sélectionnée", "Veuillez sélectionner une demande à mettre à jour.");
        }*/
    }

    private void SendSMS(RendezVous r) {
        String ACCOUNT_SID = "AC8bd6a0a5b22855c243dc60296543bf84";
        String AUTH_TOKEN = "c49c523215ae420d2cc398261c5a1b1b";

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        String twilioPhoneNumber = "+19149086373";
        String recipientPhoneNumber = "+21628749555"; // Remplacez par le numéro de téléphone du destinataire
        String messageBody = "Votre Rendez-vous est planifiee A cette Date :"+ r.getDate();
        Message message = Message.creator(
                        new PhoneNumber(recipientPhoneNumber),
                        new PhoneNumber(twilioPhoneNumber),
                        messageBody)
                .create();
    }



}