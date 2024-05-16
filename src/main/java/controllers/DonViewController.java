package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.io.IOException;
import javafx.scene.Parent;

import Entity.Don;
import service.DonService;

public class DonViewController {

    @FXML
    private TilePane cardsContainer;
    @FXML
    private Hyperlink addDonLink; // Hyperlink to add a new donation


    private DonService donService = new DonService();

    @FXML
    public void initialize() {
        setupAddDonLink();
        loadDons();
    }

    private void setupAddDonLink() {
        addDonLink.setOnAction(e -> showDonForm(null)); // Passing null for a new donation
    }

    private void showDonForm(Don don) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DonForm.fxml"));
            VBox form = loader.load();
            DonFormController controller = loader.getController();
            if (don != null) {
                controller.setDon(don); // If updating an existing donation
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(form));
            stage.setTitle(don == null ? "Ajouter un Nouveau Don" : "Modifier le Don");
            stage.showAndWait(); // Use showAndWait to refresh list after adding or updating

            reloadDons(); // Reload the list after closing the form
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void loadDons() {
        cardsContainer.getChildren().clear(); // Clear existing cards before loading new ones
        for (Don don : donService.findAll()) {
            VBox card = createDonCard(don);
            cardsContainer.getChildren().add(card);
        }
    }

    private VBox createDonCard(Don don) {
        VBox card = new VBox(5); // Reduce the spacing between elements
        card.getStyleClass().add("card-pane"); // Assign a style class for CSS styling
        card.setPadding(new Insets(10)); // Decrease padding around the elements

        // Label for the type of donation
        Label typeLabel = new Label("Type: " + don.getType());
        typeLabel.setStyle("-fx-font-weight: bold;"); // Make the font bold

        // Label for the amount of the donation
        Label amountLabel = new Label("Montant: " + (don.getMontant() == null ? "N/A" : don.getMontant() + " €"));

        // Label for the date the donation was given
        Label dateLabel = new Label("Date de remise: " + (don.getDate_remise() != null ? don.getDate_remise().toString() : "N/A"));

        // Image associated with the donation
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/images/don.PNG")));
        imageView.setFitHeight(80); // Decrease the height of the image
        imageView.setFitWidth(80); // Decrease the width of the image

        // Button to view more details about the donation
        Button detailsButton = new Button("Voir Détails");
        detailsButton.setOnAction(e -> showDonDetails(don)); // Set the action to show donation details

        // Add all components to the VBox
        card.getChildren().addAll(imageView, typeLabel, amountLabel, dateLabel, detailsButton);

        card.setAlignment(Pos.CENTER); // Center-align the content in the VBox

        return card; // Return the fully constructed card
    }
    private sampleFront sampleFront;

    public void initSampleController(sampleFront controller) {
        sampleFront = controller;
    }

    private void showDonDetails(Don don) {
      /*  try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ShowDon.fxml"));
            Parent detailsRoot = loader.load();
           ShowDonController controller = loader.getController();
            controller.setDon(don);

            // Use the scene of an existing control that's already part of the current scene
            Scene currentScene = cardsContainer.getScene();
            if (currentScene != null) {
                currentScene.setRoot(detailsRoot);
            } else {
                System.out.println("Cannot change the scene because cardsContainer is not attached to any scene.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }*/
        sampleFront.ShowDonDetails(don);
    }





    private void reloadDons() {
        // Clear and reload the donations
        cardsContainer.getChildren().clear();
        loadDons();
    }
}
