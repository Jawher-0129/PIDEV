package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import service.Usercrud;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

public class ResetPassword {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField confirmNewPasswordField;

    @FXML
    private TextField newPasswordField;

    private String email;

    public void setEmail(String email) {
        this.email = email;
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    @FXML
    void resetPassword(ActionEvent event) {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmNewPasswordField.getText();

        // Check if new password and confirm password match
        if (!newPassword.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Error", "New password and confirm password do not match.");
            return;
        }


        // Get the email from the previous scene (you need to pass it from the ForgotPassword controller)
        System.out.println("Email from ForgotPassword: " + email);
        // Update the password in the database
        Usercrud crudUtilisateurs = new Usercrud();
        crudUtilisateurs.resetPassword(email, hashPassword(newPassword));

        showAlert(Alert.AlertType.INFORMATION, "Success", "Password reset successfully.");
        switchScene("/Login.fxml", event);
    }

    @FXML
    void initialize() {
        assert confirmNewPasswordField != null : "fx:id=\"confirmNewPasswordField\" was not injected: check your FXML file 'resetPassword.fxml'.";
        assert newPasswordField != null : "fx:id=\"newPasswordField\" was not injected: check your FXML file 'resetPassword.fxml'.";
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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
