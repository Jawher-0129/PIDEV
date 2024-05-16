package controllers;

import service.ActualiteService;
import Entity.Actualite;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import org.controlsfx.control.Notifications;
import service.ExcelExporter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class ActualiteController implements Initializable {
    private ActualiteService actualiteService = new ActualiteService();
    @FXML
    private Button GotoEvent_Btn;

    @FXML
    private TextField iddescr;

    @FXML
    private ComboBox<String> idtheme;

    @FXML
    private TextField idtitre;

    @FXML
    private TextField idSearchActualite;

    @FXML
    private ComboBox<String> idtype;

    @FXML
    private TableView<Actualite> table;

    @FXML
    private TableColumn<Actualite, Integer> colId;

    @FXML
    private TableColumn<Actualite, String> colTitre;

    @FXML
    private TableColumn<Actualite, String> colDescription;

    @FXML
    private TableColumn<Actualite, String> colTypepubcible;

    @FXML
    private TableColumn<Actualite, String> colTheme;
    @FXML
    private Label titreErrorLabel;

    @FXML
    private Label descriptionErrorLabel;

    @FXML
    private Label themeErrorLabel;

    @FXML
    private Label typeErrorLabel;

    private ObservableList<Actualite> actualiteList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureTableView();
        loadData();
    }

    private void configureTableView() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id_actualite"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colTypepubcible.setCellValueFactory(new PropertyValueFactory<>("type_pub_cible"));
        colTheme.setCellValueFactory(new PropertyValueFactory<>("theme"));
    }

    private void loadData() {
        actualiteList.clear();
        actualiteList.addAll(actualiteService.getAll());
        table.setItems(actualiteList);
    }

    @FXML
    void AddActualite(ActionEvent event) {
        String titre = idtitre.getText();
        String description = iddescr.getText();
        String type_pub_cible = idtype.getValue();
        String theme = idtheme.getValue();

        boolean isValid = true;

        if (titre == null || titre.isEmpty()) {
            idtitre.setStyle("-fx-border-color: red;");
            titreErrorLabel.setText("Please enter a title.");
            titreErrorLabel.setVisible(true);
            isValid = false;
        } else {
            idtitre.setStyle("");
            titreErrorLabel.setVisible(false);
        }

        if (description == null || description.isEmpty()) {
            iddescr.setStyle("-fx-border-color: red;");
            descriptionErrorLabel.setText("Please enter a description.");
            descriptionErrorLabel.setVisible(true);
            isValid = false;
        } else {
            iddescr.setStyle("");
            descriptionErrorLabel.setVisible(false);
        }

        if (type_pub_cible == null || type_pub_cible.isEmpty()) {
            idtype.setStyle("-fx-border-color: red;");
            typeErrorLabel.setText("Please select a type.");
            typeErrorLabel.setVisible(true);
            isValid = false;
        } else {
            idtype.setStyle("");
            typeErrorLabel.setVisible(false);
        }

        if (theme == null || theme.isEmpty()) {
            idtheme.setStyle("-fx-border-color: red;");
            themeErrorLabel.setText("Please select a theme.");
            themeErrorLabel.setVisible(true);
            isValid = false;
        } else {
            idtheme.setStyle("");
            themeErrorLabel.setVisible(false);
        }

        if (isValid) {
            try {
                Actualite newActualite = new Actualite(titre, description, type_pub_cible, theme,SessionManager.getCurrentUser().getId());
                actualiteService.add(newActualite);
                loadData();
                showNotification("Successfully Added!", "Success");
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }



    @FXML
    void ClearActualite(ActionEvent event) {
        idtitre.clear();
        iddescr.clear();
        idtype.getSelectionModel().clearSelection();
        idtheme.getSelectionModel().clearSelection();

        // Reset the text field borders and hide the error labels
        idtitre.setStyle("");
        iddescr.setStyle("");
        idtype.setStyle("");
        idtheme.setStyle("");
        titreErrorLabel.setVisible(false);
        descriptionErrorLabel.setVisible(false);
        typeErrorLabel.setVisible(false);
        themeErrorLabel.setVisible(false);

        showAlert(AlertType.INFORMATION, "Success", "Success", "Cleared all fields.");
    }


    @FXML
    void DeleteActualite(ActionEvent event) {
        Actualite selectedActualite = table.getSelectionModel().getSelectedItem();
        if (selectedActualite != null) {
            int id_actualite = selectedActualite.getId_actualite();

            // Show confirmation dialog before deletion
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete this Actualite?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    actualiteService.delete(id_actualite);
                    loadData();
                    showNotification("Event successfully deleted!", "Success");
                    // Clear fields
                    idtitre.clear();
                    iddescr.clear();
                    idtype.setValue(null);
                    idtheme.setValue(null);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        } else {
            showAlert(AlertType.ERROR, "Error Message", "Error", "Please select an item to delete.");
        }
    }



    @FXML
    void UpdateActualite(ActionEvent event) {
        Actualite selectedActualite = table.getSelectionModel().getSelectedItem();
        if (selectedActualite != null) {
            int id_actualite = selectedActualite.getId_actualite();
            String titre = idtitre.getText();
            String description = iddescr.getText();
            String type_pub_cible = idtype.getValue();
            String theme = idtheme.getValue();

            boolean isValid = true;

            if (titre == null || titre.isEmpty()) {
                idtitre.setStyle("-fx-border-color: red;");
                titreErrorLabel.setText("Please enter a title.");
                titreErrorLabel.setVisible(true);
                isValid = false;
            } else {
                idtitre.setStyle(""); // Reset the style
                titreErrorLabel.setVisible(false);
            }

            if (description == null || description.isEmpty()) {
                iddescr.setStyle("-fx-border-color: red;");
                descriptionErrorLabel.setText("Please enter a description.");
                descriptionErrorLabel.setVisible(true);
                isValid = false;
            } else {
                iddescr.setStyle(""); // Reset the style
                descriptionErrorLabel.setVisible(false);
            }

            if (type_pub_cible == null || type_pub_cible.isEmpty()) {
                idtype.setStyle("-fx-border-color: red;");
                typeErrorLabel.setText("Please select a type.");
                typeErrorLabel.setVisible(true);
                isValid = false;
            } else {
                idtype.setStyle(""); // Reset the style
                typeErrorLabel.setVisible(false);
            }

            if (theme == null || theme.isEmpty()) {
                idtheme.setStyle("-fx-border-color: red;");
                themeErrorLabel.setText("Please select a theme.");
                themeErrorLabel.setVisible(true);
                isValid = false;
            } else {
                idtheme.setStyle(""); // Reset the style
                themeErrorLabel.setVisible(false);
            }

            if (isValid) {
                try {
                    Actualite updatedActualite = new Actualite(id_actualite, titre, description, type_pub_cible, theme);
                    actualiteService.update(updatedActualite, id_actualite);
                    loadData();
                    idtitre.clear();
                    iddescr.clear();
                    idtype.getSelectionModel().clearSelection();
                    idtheme.getSelectionModel().clearSelection();
                    showNotification("Successfully updated the data!", "Success");
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        } else {
            showAlert(AlertType.ERROR, "Error Message", "Error", "Please select an item to update.");
        }
    }


    @FXML
    public void selectData() {
        Actualite actualite = table.getSelectionModel().getSelectedItem();

        if (actualite != null) {
            idtitre.setText(actualite.getTitre());
            iddescr.setText(actualite.getDescription());
            idtype.getSelectionModel().select(actualite.getType_pub_cible());
            idtheme.getSelectionModel().select(actualite.getTheme());
        }
    }

    @FXML
    void SearchActualite(KeyEvent event) {
        String keyword = idSearchActualite.getText().toLowerCase().trim();

        if (keyword.isEmpty()) {
            table.setItems(actualiteList);
            return;
        }

        ObservableList<Actualite> filteredList = FXCollections.observableArrayList();

        for (Actualite actualite : actualiteList) {
            if (actualite.getTitre().toLowerCase().contains(keyword) ||
                    actualite.getDescription().toLowerCase().contains(keyword) ||
                    actualite.getType_pub_cible().toLowerCase().contains(keyword) ||
                    actualite.getTheme().toLowerCase().contains(keyword)) {
                filteredList.add(actualite);
            }
        }

        table.setItems(filteredList);
    }

    private void showAlert(AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    @FXML
    void PageEventBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/EvenementBack.fxml"));
            Parent root = loader.load();
            // Get the scene from any node in the current scene
            Scene scene = GotoEvent_Btn.getScene();
            // Set the loaded FXML file as the root of the scene
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void exportToExcel(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                ExcelExporter.exportToExcel(table, "Actualite Data", file.getAbsolutePath());
                showAlert(AlertType.INFORMATION, "Success", "Export Successful", "Data exported to Excel successfully.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Export Failed", "An error occurred while exporting data to Excel.");
        }
    }
    private void showNotification(String message, String title) {
        Notifications.create()
                .title(title)
                .text(message)
                .showInformation();
    }



}

