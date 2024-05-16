package controllers;

import Entity.Demande;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.DemandeService;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;



public class Controller implements Initializable {

    @FXML
    private GridPane demandeGrid;
    @FXML
    private ScrollPane ScrollPanee;
    @FXML
    private HBox Hboxx;

    @FXML
    private Button ConsulterRendezV;
    @FXML
    private Button PassDemande;
   private List<Demande> Demandess;
    private DemandeService demandeService;

    public Controller() {
        demandeService = new DemandeService();
    }
    private sampleFront samplefront;

    public void initSampleController(sampleFront controller) {
        samplefront = controller;
    }

    public sampleFront getSamplefront() {
        return samplefront;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       refreshDemandeGrid();
    }
    public void refreshDemandeGrid() {
        ScrollPanee.setFitToWidth(true);
        ScrollPanee.setFitToHeight(true);
       /* PassDemande.setStyle("-fx-background-color: linear-gradient(to bottom, #007bff, #0056b3);" +
                "-fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px;");*/


        // Centrage du bouton dans la HBox
        Hboxx.setAlignment(javafx.geometry.Pos.CENTER);
        demandeGrid.getChildren().clear(); // Effacer la grille actuelle
        Demandess = new ArrayList<>(demandeService.getDemandesUtilisateur(SessionManager.getCurrentUser().getId())); // Récupérer les nouvelles données
        int columns = 0;
        int row = 1;

        try {
            for (int i = 0; i < Demandess.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/ViewDemande.fxml"));

                VBox box = fxmlLoader.load();
                DemandeViewController view = fxmlLoader.getController();
                view.initSampleController(this);
                view.setData(Demandess.get(i));

                // Appliquer le style à la VBox
               /* box.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-width: 1px; -fx-padding: 10px;");
                */
                box.setAlignment(Pos.CENTER);
                demandeGrid.add(box, columns, row);
                demandeGrid.setMargin(box, new Insets(10));

                columns++;
                if (columns == 2) {
                    columns = 0;
                    row++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void PasserDemande(ActionEvent event) {
        samplefront.afficherDon();
    }
    @FXML
    void ConsulterRendezVous(ActionEvent event) {
      /*  try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RendezVousFront.fxml"));
            Parent root=loader.load();
            ConsulterRendezV.getScene().setRoot(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        samplefront.afficherRendezVous();
    }

}
