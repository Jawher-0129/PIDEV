package controllers;

import Entity.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Home {
    @FXML
    private Label NameUser;
    @FXML
    void initialize() {

        // Retrieve the current user from the session
        User currentUser = SessionManager.getCurrentUser();

        // Display the name and role of the connected user
        NameUser.setText("Welcome : " + currentUser.getNom() + " " + currentUser.getPrenom());


    }
    @FXML
    void logout(ActionEvent event) {
        // Confirm logout with a dialog box
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Logout");
        alert.setHeaderText("Logout");
        alert.setContentText("Are you sure you want to log out?");

        // Handle user's choice
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // User confirmed logout
                SessionManager.clearSession(); // Clear the session
                // Close the current window (assuming this is the only stage)
                Stage stage = (Stage) NameUser.getScene().getWindow();
                stage.close();
                // You might want to open a login window here
                // Example:
                LoginController.showLoginWindow();
            }
        });
    }
    private void switchScene(String fxmlFile, ActionEvent event) {
        try {
            System.out.println("fxml:"+ fxmlFile);

            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void EditProfile(ActionEvent event) {
        switchScene("/EditProfile.fxml", event);

    }
}
