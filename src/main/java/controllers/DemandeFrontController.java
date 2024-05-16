package controllers;


import Entity.Demande;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.DemandeService;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DemandeFrontController {

    @FXML
    private Button add;
    @FXML
    private Button Cancel;
    @FXML
    private Button add1;
    @FXML
    private Button modifier;

    @FXML
    private TextArea description;

    @FXML
    private ChoiceBox<String> don;
    @FXML
    private Button CancelModifier;

    @FXML
    private TextField titre;
    @FXML
    private Label idDemande;
    @FXML
    private TextField statut;
    private Map<Integer, String> typeDonMap = new HashMap<>();
    private DemandeService demandeService;
    @FXML
    private VBox vboxx;
    @FXML
    private Label idDon;


    public DemandeFrontController() {
        demandeService = new DemandeService();
    }
    private sampleFront samplefront;

    public void initSampleController(sampleFront controller) {
        samplefront = controller;
    }
    public void initData(int iddemande) {
        idDemande.setVisible(false);
        Demande demande =  demandeService.getById(iddemande);
       titre.setText(demande.getTitre());
       description.setText(demande.getDescription());
        idDemande.setText(String.valueOf(iddemande));

    }
    public void initidDon(int iddon) {
        idDon.setText(String.valueOf(iddon));
    }
    @FXML
    private void initializeDonChoiceBox() {
        // Récupérer les types de don avec leurs IDs depuis la base de données
        typeDonMap = demandeService.getAllTypesWithIds();

        // Ajouter les types de don à la ChoiceBox
        ObservableList<String> donValues = FXCollections.observableArrayList(typeDonMap.values());
        don.setItems(donValues);

        // Ajouter un écouteur d'événements pour récupérer le type de don sélectionné
        don.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                int idDonSelectionne = getKeyFromValue(typeDonMap, newValue);
                System.out.println("ID du don sélectionné : " + idDonSelectionne);
                // Utilisez cet ID pour l'attribut de don dans votre demande
                // Par exemple, vous pouvez l'affecter à un attribut dans votre classe de demande
                // demande.setDonId(idDonSelectionne);
            }
        });
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
       // idDemande.setVisible(false);
        statut.setText("ENCOURS DE TRAITMENT");
        statut.setEditable(false);
        initializeDonChoiceBox();

    }
    @FXML
    protected void handleAddDemande() {
       /* String donType = don.getValue(); // Récupère le type de don depuis le choix de l'utilisateur
        int donId = getKeyFromValue(typeDonMap, donType);*/ // Récupère l'ID du don correspondant au type sélectionné
        String idDonText = idDon.getText();
        int donId = Integer.parseInt(idDonText);
        // Vérifie si les champs de saisie ne sont pas vides
        if (!description.getText().isEmpty() && !titre.getText().isEmpty()) {
            if (donId != -1) { // Vérifie si l'ID du don est valide
                String descriptionText = description.getText(); // Récupère la description depuis la zone de texte
                String titreText = titre.getText();
                // Vérifie si la première lettre de la description n'est pas en majuscule
                if (!Character.isUpperCase(descriptionText.charAt(0))) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Ajout de demande", "La première lettre de la description doit être en majuscule.");
                    return;
                }

                // Vérifie si la première lettre du titre n'est pas en majuscule
                if (!Character.isUpperCase(titreText.charAt(0))) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Ajout de demande", "La première lettre du titre doit être en majuscule.");
                    return;
                }
                Demande nouvelleDemande = new Demande(donId, new Date(), descriptionText, titreText, SessionManager.getCurrentUser().getId());
                // Ajoutez la nouvelle demande via le service
                demandeService.add(nouvelleDemande);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Demande Ajout", "La demande a été ajoutée avec succès.");
                samplefront.afficherDemande();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Ajout de demande", "Veuillez sélectionner un type de don valide.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Ajout de demande", "Veuillez remplir tous les champs.");
        }
    }

    @FXML
    private void handleUpdateDemande() {
        String idDemandeText = idDemande.getText();
           int IID = Integer.parseInt(idDemandeText);
            String newTitre = titre.getText();
            String newDescription = description.getText();
            String donType = don.getValue(); // Récupère le type de don depuis le choix de l'utilisateur
            int donId = getKeyFromValue(typeDonMap, donType);
            Demande demande = new Demande(donId, new Date(), newDescription, newTitre, SessionManager.getCurrentUser().getId());
            demandeService.update(demande,IID);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Demande mise à jour", "La demande a été mise à jour avec succès.");
        samplefront.afficherDemande();
    }
    @FXML
    private void handleCancel() {
        samplefront.afficherDemande();
    }
    @FXML
    private void handleCancelM() {
        samplefront.afficherDemande();
    }
    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

}
