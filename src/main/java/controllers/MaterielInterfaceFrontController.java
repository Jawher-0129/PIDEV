package controllers;

import Entity.Materiel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import service.MaterielService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MaterielInterfaceFrontController implements Initializable {


    @FXML
    private Button retourPageCategorieFront;

    @FXML
    private TilePane materialContainer;

    private MaterielService materielService;
    private int categoryId;

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
        loadMaterialCards();
    }
    private sampleFront sampleFront;

    public void initSampleController(sampleFront controller) {
        sampleFront = controller;
    }
    public MaterielInterfaceFrontController(int categoryId) {
        this.categoryId = categoryId;
    }

    public MaterielInterfaceFrontController() {
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        materielService = new MaterielService();
        loadMaterialCards();
    }

    public void initializeWithCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    private void loadMaterialCards() {
        // Utiliser l'ID de la catégorie pour charger les matériaux associés à cette catégorie
        System.out.println(categoryId);
        List<Materiel> materials = materielService.getMaterielByCategorie(categoryId);
        for (Materiel material : materials) {
            VBox card = createMaterialCard(material);
            if (card != null) {
                materialContainer.getChildren().add(card);
            }
        }
    }

    private VBox createMaterialCard(Materiel material) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5; -fx-background-color: #ffffff; -fx-background-radius: 5;");

        Label nameLabel = new Label("Nom: " + material.getLibelleMateriel());
        nameLabel.setStyle("-fx-font-weight: bold;");

        Label descriptionLabel = new Label("Description: " + material.getDescription());

        ImageView imageView = new ImageView();
        String imageUrl = material.getImageMateriel();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                if(!(imageUrl.startsWith("C")))
                {
                    imageUrl="C:\\Users\\jawhe\\OneDrive\\Bureau\\3A32HealthSwiftIntegration (2)\\3A32HealthSwiftIntegration\\3A32HealthSwift\\public\\uploads\\" + imageUrl;
                }
                Image image = new Image(new File(imageUrl).toURI().toString());
                imageView.setImage(image);
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);
            } catch (Exception e) {
                System.out.println("Erreur lors du chargement de l'image : " + e.getMessage());
            }
        }
        card.getChildren().addAll(imageView, nameLabel, descriptionLabel);
        return card;
    }
    @FXML
    void PageCategorieFront(ActionEvent event) {
       sampleFront.RetourCategorie();
    }

}
