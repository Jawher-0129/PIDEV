package controllers;

import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;
import java.time.LocalDate;
import Entity.Campagne;
import Entity.Don;
import service.CampagneService;
import java.util.List;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import java.util.stream.Collectors;

public class CampagneController {

    @FXML private TextField titreField;
    @FXML private TextField descriptionField;
    @FXML private DatePicker dateDebutField;
    @FXML private DatePicker dateFinField;
    @FXML private TextField imageField;
    @FXML private ImageView imageView;
    @FXML private TableView<Campagne> tableView;
    @FXML private TableColumn<Campagne, Integer> colId;
    @FXML private TableColumn<Campagne, String> colTitre;
    @FXML private TableColumn<Campagne, String> colDescription;
    @FXML private TableColumn<Campagne, String> colDateDebut;
    @FXML private TableColumn<Campagne, String> colDateFin;
    @FXML private TableColumn<Campagne, String> colImage;
    @FXML
    private TextField searchField;

    private CampagneService campagneService = new CampagneService();

    private PseudoClass firstRowPseudoClass = PseudoClass.getPseudoClass("first-row");
    private sample sampleController;

    public void initSampleController(sample controller) {
        sampleController = controller;
    }
    @FXML
    void statis(ActionEvent event) {
        // switchScene("/StatController.fxml", event);
        sampleController.afficherStatCOM();
    }
    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDateDebut.setCellValueFactory(new PropertyValueFactory<>("date_debut"));
        colDateFin.setCellValueFactory(new PropertyValueFactory<>("date_fin"));
        colImage.setCellValueFactory(new PropertyValueFactory<>("image"));

        loadTableData();

        tableView.setRowFactory(tv -> {
            TableRow<Campagne> row = new TableRow<>();
            row.itemProperty().addListener((obs, previousItem, currentItem) -> {
                if (currentItem != null) {
                    row.pseudoClassStateChanged(firstRowPseudoClass, tableView.getItems().indexOf(currentItem) == 0);
                }
            });
            return row;
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fillInputFieldsWithSelectedCampaign(newSelection);
            }
        });

        searchTimeline.setCycleCount(1);
        searchTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(300), evt -> filterTable()));
        searchTimeline.setAutoReverse(false);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchTimeline.stop(); // Stop any running delay
            searchTimeline.playFromStart(); // Restart the delay
        });
    }

    private Timeline searchTimeline = new Timeline();
    @FXML
    private void filterTable() {
        String searchText = searchField.getText().toLowerCase().trim();
        if (searchText.isEmpty()) {
            tableView.setItems(FXCollections.observableArrayList(campagneService.findAll()));
            return;
        }

        ObservableList<Campagne> filteredList = FXCollections.observableArrayList(campagneService.findAll()).stream()
                .filter(campagne -> campagne.getTitre().toLowerCase().contains(searchText) ||
                        campagne.getDescription().toLowerCase().contains(searchText) ||
                        campagne.getDate_debut().contains(searchText) ||
                        campagne.getDate_fin().contains(searchText))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        tableView.setItems(filteredList);


    }

    private boolean validateInput() {
        String errorMessage = "";

        if (titreField.getText() == null || titreField.getText().isEmpty()) {
            errorMessage += "Title is required.\n";
        }
        if (descriptionField.getText() == null || descriptionField.getText().isEmpty()) {
            errorMessage += "Description is required.\n";
        }
        if (dateDebutField.getValue() == null || dateDebutField.getValue().isBefore(LocalDate.now())) {
            errorMessage += "Start date must be today or later.\n";
        }
        if (dateFinField.getValue() == null || (dateDebutField.getValue() != null && dateFinField.getValue().isBefore(dateDebutField.getValue()))) {
            errorMessage += "End date must be after the start date.\n";
        }
        if (imageField.getText() == null || imageField.getText().isEmpty() || !new File(imageField.getText()).exists()) {
            errorMessage += "A valid image file must be selected.\n";
        }

        if (!errorMessage.isEmpty()) {
            showAlert("Validation Error", errorMessage);
            return false;
        }
        return true;
    }

    private void fillInputFieldsWithSelectedCampaign(Campagne selected) {
        titreField.setText(selected.getTitre());
        descriptionField.setText(selected.getDescription());
        dateDebutField.setValue(LocalDate.parse(selected.getDate_debut()));
        dateFinField.setValue(LocalDate.parse(selected.getDate_fin()));
        imageField.setText(selected.getImage());
        if (selected.getImage() != null && !selected.getImage().isEmpty()) {
            File file = new File(selected.getImage());
            if (file.exists()) {
                imageView.setImage(new Image(file.toURI().toString()));
            }
        }
    }

    private void loadTableData() {
        ObservableList<Campagne> observableList = FXCollections.observableArrayList(campagneService.findAll());
        tableView.setItems(observableList);
    }

    @FXML
    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            imageField.setText(file.getAbsolutePath());
            imageView.setImage(new Image(file.toURI().toString()));
        }
    }

    @FXML
    private void insert() {
        if (validateInput()) {
            Campagne campagne = new Campagne(
                    titreField.getText(),
                    descriptionField.getText(),
                    dateDebutField.getValue().toString(),
                    dateFinField.getValue().toString(),
                    imageField.getText()
            );
            Campagne savedCampagne = campagneService.save(campagne);
            if (savedCampagne != null) {
                tableView.getItems().add(savedCampagne);
                clearFields();
                showAlert("Success", "Campagne added successfully.");
            } else {
                showAlert("Error", "Failed to add campagne.");
            }
        }
    }

    @FXML
    private void update() {
        if (validateInput()) {
            Campagne selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selected.setTitre(titreField.getText());
                selected.setDescription(descriptionField.getText());
                selected.setDate_debut(dateDebutField.getValue().toString());
                selected.setDate_fin(dateFinField.getValue().toString());
                selected.setImage(imageField.getText());
                if (campagneService.update(selected) != null) {
                    tableView.refresh();
                    showAlert("Success", "Campagne updated successfully.");
                } else {
                    showAlert("Error", "Failed to update campagne.");
                }
            } else {
                showAlert("Error", "No campagne selected.");
            }
        }
    }

    @FXML
    private void delete() {
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Campagne selected = tableView.getItems().get(selectedIndex);
            campagneService.deleteById(selected.getId());
            tableView.getItems().remove(selectedIndex);
            showAlert("Success", "Campagne deleted successfully.");
        } else {
            showAlert("Error", "No campagne selected.");
        }
    }


    @FXML
    private void clearFields() {
        titreField.clear();
        descriptionField.clear();
        dateDebutField.setValue(null);
        dateFinField.setValue(null);
        imageField.clear();
        imageView.setImage(null);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
