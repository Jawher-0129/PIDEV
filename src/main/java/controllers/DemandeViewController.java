package controllers;



import Entity.Demande;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import service.DemandeService;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;


public class DemandeViewController {
    @FXML
    private VBox VboxDemande;
    private DemandeService demandeService;

    public DemandeViewController() {
        demandeService = new DemandeService();
    }


    @FXML
    private Label description;

    @FXML
    private Label titre;

    @FXML
    private Label id;

    @FXML
    private Button modifier;
    @FXML
    private Label date;

    @FXML
    private Button supprimer;

  public void setData(Demande demande){
      titre.setText(demande.getTitre());
      description.setText(demande.getDescription());
      id.setVisible(false);
      id.setText(String.valueOf(demande.getId_demande()));
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
      String formattedDate = dateFormat.format(demande.getDate());
      date.setText(formattedDate);
      titre.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
      description.setStyle("-fx-font-size: 12px;");
      // Style pour le titre
      /*

      // Style pour la description


      // Style pour l'ID
      id.setStyle("-fx-font-size: 10px;");

      // Style pour le bouton modifier
      modifier.setStyle("-fx-background-color: linear-gradient(to bottom, #007bff, #0056b3);" +
              "-fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 8px 16px;");

      // Style pour le bouton supprimer
      supprimer.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 8px 16px;");*/
  }
    private Controller samplefrontt;

    public void initSampleController(Controller controller) {
        samplefrontt = controller;
    }
    @FXML
    protected void handleDeleteDemande() {
        // Demander confirmation à l'utilisateur avant de supprimer la demande
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer la demande");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette demande ?");

        // Attendre la réponse de l'utilisateur
        Optional<ButtonType> result = confirmation.showAndWait();

        // Si l'utilisateur a confirmé la suppression
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Supprimer la demande à partir du service
            int idD = Integer.parseInt(id.getText());
            // Utilisez la méthode delete avec l'ID extrait
            demandeService.delete(idD);
            samplefrontt.getSamplefront().afficherDemande();

        }
    }

    @FXML
    void passerAModifier() {
        int demandeId = Integer.parseInt(id.getText());
       /* try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierDemande.fxml"));
            Parent root = loader.load();
            modifier.getScene().setRoot(root);
            DemandeFrontController demandeFrontController = loader.getController();
            demandeFrontController.initData(demandeId); // Passer l'ID de la demande au contrôleur

        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
       // samplefront.PasserModifier(demandeId);
        samplefrontt.getSamplefront().PasserModifier(demandeId);
    }
    @FXML
    void PageQrCode(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierDemande.fxml"));
            Parent root=loader.load();
            modifier.getScene().setRoot(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
