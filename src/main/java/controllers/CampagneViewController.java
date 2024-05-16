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
import java.io.IOException;
import java.util.List;
import Entity.Campagne;
import service.CampagneService;
import javafx.scene.Parent;
import javafx.geometry.Pos;


public class CampagneViewController {

    @FXML
    private TilePane campagneContainer;
    @FXML
    private Button addCampagneButton;  // Assuming there's a button to add campaigns
    private CampagneService campagneService = new CampagneService();

    @FXML
    public void initialize() {
        setupAddCampagneButton();
        loadCampagneCards();
        if(SessionManager.getCurrentUser().getRoles().equals("[\"ROLE_DONATEUR\"]")){
            addCampagneButton.setVisible(false);
        }
    }

    private void setupAddCampagneButton() {
        addCampagneButton.setOnAction(e -> showCampagneForm(null)); // Passing null for a new campaign
    }

    private void showCampagneForm(Campagne campagne) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CampagneForm.fxml"));
            ScrollPane form = loader.load(); // Cast to ScrollPane if the FXML root is ScrollPane
            CampagneFormController controller = loader.getController();
            if (campagne != null) {
                controller.setCampagne(campagne); // If updating an existing campaign
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(form)); // Ensure the Scene is set with the correct root type
            stage.setTitle(campagne == null ? "Ajouter une Nouvelle Campagne" : "Modifier la Campagne");
            stage.showAndWait(); // Use showAndWait to refresh list after adding or updating
            reloadCampagnes(); // Reload the list after closing the form
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    private void loadCampagneCards() {
        campagneContainer.getChildren().clear(); // Clear existing cards before loading new ones
        List<Campagne> campagnes = campagneService.findAll();
        for (Campagne campagne : campagnes) {
            VBox card = createCampagneCard(campagne);
            campagneContainer.getChildren().add(card);
        }
    }

    private VBox createCampagneCard(Campagne campagne) {
        VBox card = new VBox(10);
        card.getStyleClass().add("campaign-card");

        // Image
        ImageView imageView = new ImageView();

        String imageurl;
        if (campagne.getImage() != null && !campagne.getImage().isEmpty()) {
            if(!(campagne.getImage().startsWith("C")))
            {
                imageurl="C:\\Users\\jawhe\\OneDrive\\Bureau\\3A32HealthSwiftIntegration (2)\\3A32HealthSwiftIntegration\\3A32HealthSwift\\public\\uploads\\Images\\"+campagne.getImage();
            }
            else
                imageurl=campagne.getImage();

            imageView.setImage(new Image("file:" + imageurl, true)); // true to load in background
            imageView.setFitHeight(120); // Adjust height as needed
            imageView.setFitWidth(200); // Adjust width as needed
        }
        imageView.getStyleClass().add("campaign-image");

        // Title with updated styling
        Label titleLabel = new Label(campagne.getTitre());
        titleLabel.getStyleClass().add("campaign-title");

        // Subtitle
        Label subtitleLabel = new Label("Participe Maintenant!");
        subtitleLabel.getStyleClass().add("campaign-subtitle");

        // Date Range
        Label dateLabel = new Label("Commence: " + campagne.getDate_debut().toString() +
                " jusqu'au: " + campagne.getDate_fin().toString());
        dateLabel.getStyleClass().add("campaign-date");

        // Details button
        Button detailsButton = new Button("Détails");
        detailsButton.getStyleClass().add("campaign-details-button");
        detailsButton.setOnAction(e -> showCampagneDetails(campagne));

        // Reordering: First add the image, then the title
        card.getChildren().addAll(imageView, titleLabel, subtitleLabel, dateLabel, detailsButton);
        card.setAlignment(Pos.CENTER); // Center content

        return card;
    }




    private sampleFront sampleFront;

    public void initSampleController(sampleFront controller) {
        sampleFront = controller;
    }
    private void showCampagneDetails(Campagne campagne) {
       /* try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ShowCampagne.fxml"));
            Parent detailsRoot = loader.load();
            ShowCampagneController controller = loader.getController();
            controller.setCampagne(campagne);

            // Use the scene of an existing control that's already part of the current scene
            Scene currentScene = campagneContainer.getScene();
            if (currentScene != null) {
                currentScene.setRoot(detailsRoot);
            } else {
                System.out.println("Cannot change the scene because campagneContainer is not attached to any.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }*/
        sampleFront.ShowCampagneDetails(campagne);
    }





    private void reloadCampagnes() {
        // Clear and reload the campaigns
        campagneContainer.getChildren().clear();
        loadCampagneCards();
    }
}
