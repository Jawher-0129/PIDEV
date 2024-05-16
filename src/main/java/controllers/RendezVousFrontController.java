package controllers;


import Entity.RendezVous;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.RendezVousService;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RendezVousFrontController implements Initializable {
    @FXML
    private GridPane demandeGrid;
    @FXML
    private ScrollPane ScrollPanee;
    private List<RendezVous> RendezVouss;
    private RendezVousService rendezVousService;

    public RendezVousFrontController() {
        rendezVousService = new RendezVousService();
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



        demandeGrid.getChildren().clear(); // Effacer la grille actuelle
        RendezVouss = new ArrayList<>(rendezVousService.getAllByUtilisateur(SessionManager.getCurrentUser().getId())); // Récupérer les nouvelles données
        int columns = 0;
        int row = 1;

        try {
            for (int i = 0; i < RendezVouss.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/cardRendezvous.fxml"));

                VBox box = fxmlLoader.load();
                cardRendezvous view = fxmlLoader.getController();
                view.setData(RendezVouss.get(i));

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
}
