package controllers;

import Entity.Actualite;
import Entity.Evenement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import service.EvenementService;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class EvenementController implements Initializable {
    private EvenementService evenementService;

    @FXML
    private Button Statistique;

    @FXML
    private Button GotoActualite_Btn;

    @FXML
    private Button UpdateEvent_btn;

    @FXML
    private Button addEvent_btn;

    @FXML
    private Button clearEvent_Btn;

    @FXML
    private TableColumn<Evenement, String> colDateEvent;

    @FXML
    private TableColumn<Evenement, Integer> colDureeEvent;

    @FXML
    private TableColumn<Evenement, Integer> colIdEvent;

    @FXML
    private TableColumn<Evenement, String> colImageEvent;

    @FXML
    private TableColumn<Evenement, String> colLieuEvent;

    @FXML
    private TableColumn<Evenement, String> colObjEvent;

    @FXML
    private TableColumn<Evenement, String> colTitreEvent;

    @FXML
    private TableColumn<Evenement, Integer> colActualiteId;

    @FXML
    private Button deleteEvent_btn;

    @FXML
    private TextField enent_title;

    @FXML
    private DatePicker event_date;

    @FXML
    private TextField event_duree;

    @FXML
    private Button event_importBtn;

    @FXML
    private TextField event_lieu;

    @FXML
    private TextField event_obj;

    @FXML
    private TextField chercherEvent;

    @FXML
    private ImageView imageView;

    @FXML
    private ComboBox<String> ActualiteRelated;

    @FXML
    private TableView<Evenement> tableEvent;
    private ObservableList<Evenement> evementList;
    @FXML
    private Label titleErrorLabel;

    @FXML
    private Label dateErrorLabel;

    @FXML
    private Label dureeErrorLabel;

    @FXML
    private Label lieuErrorLabel;
    @FXML
    private Label objErrorLabel;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        evenementService = new EvenementService();
        configureTableView();
        loadData();
        loadActualiteIDs();
    }

    private void loadData() {
        evementList = FXCollections.observableArrayList(evenementService.getAll());
        tableEvent.setItems(evementList);
    }


    private void configureTableView() {
        colIdEvent.setCellValueFactory(new PropertyValueFactory<>("id_evenement"));
        colTitreEvent.setCellValueFactory(new PropertyValueFactory<>("Titre"));
        colDateEvent.setCellValueFactory(new PropertyValueFactory<>("Date"));
        colDureeEvent.setCellValueFactory(new PropertyValueFactory<>("Duree"));
        colLieuEvent.setCellValueFactory(new PropertyValueFactory<>("lieu"));
        colObjEvent.setCellValueFactory(new PropertyValueFactory<>("Objectif"));
        colImageEvent.setCellValueFactory(new PropertyValueFactory<>("image"));
        colActualiteId.setCellValueFactory(new PropertyValueFactory<>("id_actualite"));
    }

    @FXML
    void AddEvent(ActionEvent event) {
        // Retrieve the selected Actualite from the ComboBox
        String selectedActualite = ActualiteRelated.getSelectionModel().getSelectedItem();

        // Retrieve the Actualite ID from the database based on its title
        int actualiteId = -1; // Initialize with -1 as default value
        if (selectedActualite != null) {
            // Retrieve actualiteId from the service
            actualiteId = evenementService.getActualiteIdByTitle(selectedActualite);
        }

        // Initialize error flags
        boolean isValid = true;

        // Validate and show errors for each field
        if (enent_title.getText().isEmpty()) {
            enent_title.setStyle("-fx-border-color: red;");
            // Show error label
            titleErrorLabel.setText("Please enter a title.");
            titleErrorLabel.setVisible(true);
            isValid = false;
        } else {
            enent_title.setStyle("");
            // Hide error label
            titleErrorLabel.setVisible(false);
        }

        if (event_date.getValue() == null) {
            event_date.setStyle("-fx-border-color: red;");
            // Show error label
            dateErrorLabel.setText("Please select a date.");
            dateErrorLabel.setVisible(true);
            isValid = false;
        } else {
            event_date.setStyle("");
            // Hide error label
            dateErrorLabel.setVisible(false);
        }

        if (event_duree.getText().isEmpty()) {
            event_duree.setStyle("-fx-border-color: red;");
            // Show error label
            dureeErrorLabel.setText("Please enter a duration.");
            dureeErrorLabel.setVisible(true);
            isValid = false;
        } else {
            event_duree.setStyle("");
            // Hide error label
            dureeErrorLabel.setVisible(false);
        }
        if (event_lieu.getText().isEmpty()) {
            event_lieu.setStyle("-fx-border-color: red;");
            // Show error label
            lieuErrorLabel.setText("Please enter a location.");
            lieuErrorLabel.setVisible(true);
            isValid = false;
        } else {
            event_lieu.setStyle("");
            // Hide error label
            lieuErrorLabel.setVisible(false);
        }

        if (event_obj.getText().isEmpty()) {
            event_obj.setStyle("-fx-border-color: red;");
            // Show error label
            objErrorLabel.setText("Please enter an objective.");
            objErrorLabel.setVisible(true);
            isValid = false;
        } else {
            event_obj.setStyle("");
            // Hide error label
            objErrorLabel.setVisible(false);
        }

        // Validate duree
        try {
            int dureeValue = Integer.parseInt(event_duree.getText());
            if (dureeValue < 0) {
                showAlert(Alert.AlertType.ERROR, "Error Message", null, "Duration must be a non-negative integer.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error Message", null, "Duration must be a valid integer.");
            return;
        }



        // cbn fama image
        if (selectedImagePath == null) {
            showAlert(Alert.AlertType.ERROR, "Error Message", null, "Please select an image.");
            return;
        }

        if (isValid) {
            try {
                if (event_date.getValue().isBefore(LocalDate.now())) {
                    showAlert(Alert.AlertType.ERROR, "Error Message", null, "Event date must be in the future.");
                    return;
                }

                Evenement newEvenement = new Evenement( enent_title.getText(), java.sql.Date.valueOf(event_date.getValue()),
                        Integer.parseInt(event_duree.getText()), event_lieu.getText(), event_obj.getText(),
                        selectedImagePath, actualiteId,SessionManager.getCurrentUser().getId());

                evenementService.add(newEvenement);
                showNotification("Successfully Added!", "Success");
                ClearEvent(null);
                loadData();
            } catch (IllegalArgumentException e) {
                showAlert(Alert.AlertType.ERROR, "Error Message", null, e.getMessage());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error Message", null, "An error occurred while adding the event.");
                e.printStackTrace();
            }
        }
    }


    @FXML
    void ClearEvent(ActionEvent event) {
        enent_title.clear();
        event_duree.clear();
        event_lieu.clear();
        event_obj.clear();
        event_date.setValue(null);
        imageView.setImage(null);
        ActualiteRelated.getSelectionModel().clearSelection();
        showAlert(Alert.AlertType.INFORMATION, "Information", "Data Cleared", "Event data has been cleared.");
        // Reset the text field borders and hide the error labels
        enent_title.setStyle("");
        event_date.setStyle("");
        event_duree.setStyle("");
        event_lieu.setStyle("");
        event_obj.setStyle("");
        titleErrorLabel.setVisible(false);
        dateErrorLabel.setVisible(false);
        dureeErrorLabel.setVisible(false);
        lieuErrorLabel.setVisible(false);
        objErrorLabel.setVisible(false);
    }

    private sample sampleController;

    public void initSampleController(sample controller) {
        sampleController = controller;
    }
    @FXML
    void statis(ActionEvent event) {
        // switchScene("/StatController.fxml", event);
        sampleController.afficherStatEVENTT();
    }

    @FXML
    void DeleteEvent(ActionEvent event) {
        Evenement selectedEvent = tableEvent.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Please select an event to delete.");
            return;
        }

        // Show confirmation dialog before deletion
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this Event?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                evenementService.delete(selectedEvent.getId_evenement());
                showNotification("Event successfully deleted!", "Success");

                // Clear fields
                enent_title.clear();
                event_date.setValue(null);
                event_duree.clear();
                event_lieu.clear();
                event_obj.clear();
                imageView.setImage(null);

                loadData();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    void UpdateEvent(ActionEvent event) {
        Evenement selectedEvent = tableEvent.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Please select an event to update.");
            return;
        }

        // Initialize error flags
        boolean isValid = true;

        // Validate and show errors for each field
        if (enent_title.getText().isEmpty()) {
            enent_title.setStyle("-fx-border-color: red;");
            titleErrorLabel.setText("Please enter a title.");
            titleErrorLabel.setVisible(true);
            isValid = false;
        } else {
            enent_title.setStyle("");
            titleErrorLabel.setVisible(false);
        }

        if (event_date.getValue() == null) {
            event_date.setStyle("-fx-border-color: red;");
            dateErrorLabel.setText("Please select a date.");
            dateErrorLabel.setVisible(true);
            isValid = false;
        } else {
            event_date.setStyle("");
            dateErrorLabel.setVisible(false);
        }

        if (event_duree.getText().isEmpty()) {
            event_duree.setStyle("-fx-border-color: red;");
            dureeErrorLabel.setText("Please enter a duration.");
            dureeErrorLabel.setVisible(true);
            isValid = false;
        } else {
            event_duree.setStyle("");
            dureeErrorLabel.setVisible(false);
        }
        if (event_lieu.getText().isEmpty()) {
            event_lieu.setStyle("-fx-border-color: red;");
            lieuErrorLabel.setText("Please enter a location.");
            lieuErrorLabel.setVisible(true);
            isValid = false;
        } else {
            event_lieu.setStyle("");
            lieuErrorLabel.setVisible(false);
        }

        if (event_obj.getText().isEmpty()) {
            event_obj.setStyle("-fx-border-color: red;");
            objErrorLabel.setText("Please enter an objective.");
            objErrorLabel.setVisible(true);
            isValid = false;
        } else {
            event_obj.setStyle("");
            objErrorLabel.setVisible(false);
        }

        // Validate duree positive + int
        try {
            int dureeValue = Integer.parseInt(event_duree.getText());
            if (dureeValue < 0) {
                showAlert(Alert.AlertType.ERROR, "Error Message", null, "Duration must be a non-negative integer.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error Message", null, "Duration must be a valid integer.");
            return;
        }
        // date akber ml lyoum
        if (event_date.getValue().isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.ERROR, "Error Message", null, "Event date must be in the future.");
            return;
        }

        // cbn fama image
        if (selectedImagePath == null) {
            showAlert(Alert.AlertType.ERROR, "Error Message", null, "Please select an image.");
            return;
        }

        // Retrieve the selected Actualite from the ComboBox
        String selectedActualite = ActualiteRelated.getSelectionModel().getSelectedItem();

        // Retrieve the Actualite ID from the database based on its title
        int actualiteId = -1; // Initialize with -1 as default value
        if (selectedActualite != null) {
            // Retrieve actualiteId from the service
            actualiteId = evenementService.getActualiteIdByTitle(selectedActualite);
        }

        // Update the event if all fields are valid
        if (isValid) {
            try {
                Evenement updatedEvenement = new Evenement(selectedEvent.getId_evenement(), enent_title.getText(),
                        java.sql.Date.valueOf(event_date.getValue()), Integer.parseInt(event_duree.getText()),
                        event_lieu.getText(), event_obj.getText(), selectedImagePath, actualiteId);

                evenementService.update(updatedEvenement, selectedEvent.getId_evenement());
                showNotification("Successfully updated the data!", "Success");
                loadData();
            } catch (IllegalArgumentException e) {
                showAlert(Alert.AlertType.ERROR, "Error Message", null, e.getMessage());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error Message", null, "An error occurred while updating the event.");
                e.printStackTrace();
            }
        }
    }




    private String selectedImagePath;

    @FXML
    void importImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        Window window = ((Node) event.getTarget()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(window);
        if (selectedFile != null) {
            selectedImagePath = selectedFile.getAbsolutePath();
            Image image = new Image(selectedFile.toURI().toString());
            imageView.setImage(image);
        }
    }

    @FXML
    public void selectData() {
        Evenement evenement = tableEvent.getSelectionModel().getSelectedItem();

        if (evenement == null)
            return;

        enent_title.setText(evenement.getTitre());
        event_duree.setText(String.valueOf(evenement.getDuree()));
        event_lieu.setText(evenement.getLieu());
        event_obj.setText(evenement.getObjectif());
        event_date.setValue(LocalDate.parse(String.valueOf(evenement.getDate())));

        String imagePath = evenement.getImage();
        if (imagePath != null && !imagePath.isEmpty()) {
            String imageurl;
            if(!(evenement.getImage().startsWith("C")))
            {
                imageurl="C:\\Users\\jawhe\\OneDrive\\Bureau\\3A32HealthSwiftIntegration (2)\\3A32HealthSwiftIntegration\\3A32HealthSwift\\public\\uploads\\Images\\"+evenement.getImage();
            }
            else
                imageurl=evenement.getImage();
            File file = new File(imageurl);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Image Not Found", "The selected image file does not exist.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Image Path Not Provided", "The image path for the selected event is not provided.");
        }

        ActualiteRelated.setValue(String.valueOf(evenement.getId_actualite()));
    }

    @FXML
    void chercherEvenement(KeyEvent event) {
        String searchValue = chercherEvent.getText();
        ObservableList<Evenement> filteredList = FXCollections.observableArrayList();

        for (Evenement evenement : evementList) {
            String titre = evenement.getTitre().toLowerCase();
            String date = evenement.getDate().toString().toLowerCase();
            String duree = String.valueOf(evenement.getDuree());
            String lieu = evenement.getLieu().toLowerCase();
            String objectif = evenement.getObjectif().toLowerCase();

            if (titre.contains(searchValue.toLowerCase()) ||
                    date.contains(searchValue.toLowerCase()) ||
                    duree.contains(searchValue.toLowerCase()) ||
                    lieu.contains(searchValue.toLowerCase()) ||
                    objectif.contains(searchValue.toLowerCase())) {
                filteredList.add(evenement);
            }
        }

        tableEvent.setItems(filteredList);
    }

    private void loadActualiteIDs() {
        ActualiteRelated.getItems().clear();
        ActualiteRelated.getItems().addAll(evenementService.getAllActualiteTitles());
    }

    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    @FXML
    void PageActualiteBack(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/ActualiteBack.fxml"));
            Parent root = loader.load();
            // Get the scene from any node in the current scene
            Scene scene = GotoActualite_Btn.getScene();
            // Set the loaded FXML file as the root of the scene
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void showNotification(String message, String title) {
        Notifications.create()
                .title(title)
                .text(message)
                .showInformation();
    }
}

