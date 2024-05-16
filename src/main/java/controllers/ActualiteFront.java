package controllers;

import Entity.Actualite;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import service.ActualiteService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ActualiteFront implements Initializable {

    @FXML
    private TilePane cardsContainer;

    private ActualiteService actualiteService = new ActualiteService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadActualites();
        animateActualites();
    }

    private void loadActualites() {
        List<Actualite> actualites = actualiteService.getAll();
        for (Actualite actualite : actualites) {
            VBox card = createActualiteCard(actualite);
            cardsContainer.getChildren().add(card);
        }
    }

    private VBox createActualiteCard(Actualite actualite) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #f0f0f0; " +
                "-fx-padding: 10px; " +
                "-fx-spacing: 10px; " +
                "-fx-alignment: center; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0.5, 0, 0); " +
                "-fx-border-radius: 10px; " +
                "-fx-background-radius: 10px; " +
                "-fx-min-width: 200px; " +
                "-fx-max-width: 200px; " +
                "-fx-pref-height: 200px;");

        // Load the image once
        Image image = new Image(getClass().getResourceAsStream("/ActualiteLogo.jpg")); // Adjust the path as per your project structure
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(100); // Adjust width as needed
        imageView.setFitHeight(100); // Adjust height as needed

        Label titleLabel = new Label("Titre: " + actualite.getTitre());
        titleLabel.getStyleClass().add("campaign-title"); // Apply the title style class
        Label descriptionLabel = new Label("Description: " + actualite.getDescription());
        descriptionLabel.getStyleClass().add("campaign-subtitle");
        Label typeLabel = new Label("Type Pub Cible: " + actualite.getType_pub_cible());
        typeLabel.getStyleClass().add("campaign-subtitle");
        Label themeLabel = new Label("Theme: " + actualite.getTheme());
        themeLabel.getStyleClass().add("campaign-subtitle");

        card.getChildren().addAll(imageView, titleLabel, descriptionLabel, typeLabel, themeLabel);

        return card;
    }
    private void animateActualites() {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(10), cardsContainer);
        transition.setByY(-200);
        transition.setCycleCount(TranslateTransition.INDEFINITE);
        transition.play();
    }


}