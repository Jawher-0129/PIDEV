package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import Entity.Campagne;
import Entity.Don;
import service.CampagneService;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CampagneFormController {

    @FXML private TextField titreField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker debutDatePicker;
    @FXML private DatePicker finDatePicker;
    @FXML private TextField imageField;
    @FXML private ImageView imageView;
    @FXML private Button boutonEnregistrer;
    @FXML private Label labelTitre;
    @FXML private Label titreError, descriptionError, debutError, finError, imageError;

    private CampagneService service;
    private Campagne currentCampagne;

    @FXML
    public void initialize() {
        service = new CampagneService();
        if (currentCampagne != null) {
            loadCampagneDetails();
        } else {
            labelTitre.setText("Créer une Nouvelle Campagne");
        }
    }

    public void setCampagne(Campagne campagne) {
        this.currentCampagne = campagne;
        if (campagne != null) {
            loadCampagneDetails();
            labelTitre.setText("Modifier la Campagne");
        } else {
            labelTitre.setText("Créer une Nouvelle Campagne");
        }
    }

    private void loadCampagneDetails() {
        titreField.setText(currentCampagne.getTitre());
        descriptionArea.setText(currentCampagne.getDescription());
        debutDatePicker.setValue(LocalDate.parse(currentCampagne.getDate_debut(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        finDatePicker.setValue(LocalDate.parse(currentCampagne.getDate_fin(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        String imageurl;
        if(!(currentCampagne.getImage().startsWith("C")))
        {
            imageurl="C:\\Users\\jawhe\\OneDrive\\Bureau\\3A32HealthSwiftIntegration (2)\\3A32HealthSwiftIntegration\\3A32HealthSwift\\public\\uploads\\Images\\"+currentCampagne.getImage();
        }
        else
            imageurl=currentCampagne.getImage();
        imageField.setText(imageurl);
    }

    @FXML
    private void handleSave() {
        if (validateInput()) {
            boolean isNew = currentCampagne == null;
            if (isNew) {
             /*   currentCampagne = new Campagne(
                        titreField.getText(),
                        descriptionArea.getText(),
                        debutDatePicker.getValue().toString(),
                        finDatePicker.getValue().toString(),
                        imageField.getText()
                );*/
                currentCampagne = new Campagne(
                        debutDatePicker.getValue().toString(),
                        finDatePicker.getValue().toString(),
                        descriptionArea.getText(),SessionManager.getCurrentUser().getId(),
                        imageField.getText(),  titreField.getText()
                );

            } else {
                currentCampagne.setTitre(titreField.getText());
                currentCampagne.setDescription(descriptionArea.getText());
                currentCampagne.setDate_debut(debutDatePicker.getValue().toString());
                currentCampagne.setDate_fin(finDatePicker.getValue().toString());
                currentCampagne.setImage(imageField.getText());
            }

            if (isNew) {
                service.save(currentCampagne);
                showAlert("Success", "New campaign created successfully!");
            } else {
                service.update(currentCampagne);
                showAlert("Success", "Campaign updated successfully!");
            }
            closeStage();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean validateInput() {
        boolean isValid = true;
        clearErrors();

        if (titreField.getText() == null || titreField.getText().isEmpty()) {
            titreError.setText("Title is required.");
            titreField.getStyleClass().add("error");
            isValid = false;
        } else {
            titreField.getStyleClass().remove("error");
        }

        if (descriptionArea.getText() == null || descriptionArea.getText().isEmpty()) {
            descriptionError.setText("Description is required.");
            descriptionArea.getStyleClass().add("error");
            isValid = false;
        } else {
            descriptionArea.getStyleClass().remove("error");
        }

        if (debutDatePicker.getValue() == null || debutDatePicker.getValue().isBefore(LocalDate.now())) {
            debutError.setText("Start date must be today or later.");
            debutDatePicker.getStyleClass().add("error");
            isValid = false;
        } else {
            debutDatePicker.getStyleClass().remove("error");
        }

        if (finDatePicker.getValue() == null || (debutDatePicker.getValue() != null && finDatePicker.getValue().isBefore(debutDatePicker.getValue()))) {
            finError.setText("End date must be after the start date.");
            finDatePicker.getStyleClass().add("error");
            isValid = false;
        } else {
            finDatePicker.getStyleClass().remove("error");
        }

        if (imageField.getText() == null || imageField.getText().isEmpty() || !new File(imageField.getText()).exists()) {
            imageError.setText("A valid image file must be selected.");
            imageField.getStyleClass().add("error");
            isValid = false;
        } else {
            imageField.getStyleClass().remove("error");
        }

        return isValid;
    }

    private void clearErrors() {
        titreError.setText("");
        descriptionError.setText("");
        debutError.setText("");
        finError.setText("");
        imageError.setText("");

        titreField.getStyleClass().remove("error");
        descriptionArea.getStyleClass().remove("error");
        debutDatePicker.getStyleClass().remove("error");
        finDatePicker.getStyleClass().remove("error");
        imageField.getStyleClass().remove("error");
    }

    @FXML
    private void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image pour la campagne");
        File file = fileChooser.showOpenDialog(imageField.getScene().getWindow());
        if (file != null) {
            imageField.setText(file.getAbsolutePath());
            imageView.setImage(new Image(file.toURI().toString()));
        }
    }

    private void closeStage() {
        Stage stage = (Stage) titreField.getScene().getWindow();
        stage.close();
    }
}
