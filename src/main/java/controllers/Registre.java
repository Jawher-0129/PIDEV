package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.paint.Color;
import Entity.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import service.Usercrud;
import service.SystemNotification;
import static service.password.hashPassword;
public class Registre {
    @FXML
    private Label nomErrorLabel;

    @FXML
    private Label prenomErrorLabel;

    @FXML
    private Label emailErrorLabel;

    @FXML
    private Label adresseErrorLabel;

    @FXML
    private Label telephoneErrorLabel;

    @FXML
    private Label passwordErrorLabel;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField Adresse;

    @FXML
    private TextField email;

    @FXML
    private Label lblErrors;

    @FXML
    private Button loginButton;

    @FXML
    private TextField nom;

    @FXML
    private PasswordField password;

    @FXML
    private TextField prenom;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private TextField telephone;



    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    @FXML
    void Registre(ActionEvent event) {

        String nom = this.nom.getText();
        String prenom = this.prenom.getText();
        String email = this.email.getText();
        String adresse = this.Adresse.getText();

        String telephone = this.telephone.getText();

        String password = this.password.getText();

        if (!checkFieldsNotEmpty()) {
            return;
        }
        if (!isValidEmail(email)) {
            setErrorLabel(emailErrorLabel, " invalid email address");
            return;
        }
        if (new Usercrud().isEmailExistsInDatabase(email)) {
            // Display an error message in red in the email error label
            setErrorLabel(emailErrorLabel, "Email already exists");
            return;
        }

        String selectedRole = roleComboBox.getValue();
        if (selectedRole == null || selectedRole.isEmpty()) {
            // Display an alert if no role is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Please select a role");
            alert.show();
            return;
        }


        String hashedPassword = hashPassword(password);

        User u=new User();
        if(selectedRole.equals("Donateur"))
        {
            selectedRole = "[\"" + "ROLE_DONATEUR" + "\"]";
            u.setRoles(selectedRole);
        }
        else if(selectedRole.equals("Directeur De Campagne"))
        {
            selectedRole = "[\"" + "ROLE_DIRECTEUR" + "\"]";
            u.setRoles(selectedRole);
        }

        User newUser = new User(nom, prenom, email, hashedPassword, adresse, telephone, selectedRole);

        Usercrud add = new Usercrud();
        add.addUser(newUser);

        SystemNotification.showNotification("New User Registered", "A new user has been registered.");

        switchScene("/Login.fxml", event);


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
    void handleButtonAction(MouseEvent event) {

    }
    @FXML
    void Login(ActionEvent event) {
        switchScene("/Login.fxml", event);

    }
    @FXML
    void initialize() {

    }
    private void setErrorLabel(Label label, String message) {
        label.setTextFill(Color.RED);
        label.setText(message);
    }

    private void clearErrorLabels() {
        nomErrorLabel.setText("");
        nomErrorLabel.setTextFill(Color.BLACK);
        prenomErrorLabel.setText("");
        prenomErrorLabel.setTextFill(Color.BLACK);
        emailErrorLabel.setText("");
        emailErrorLabel.setTextFill(Color.BLACK);
        adresseErrorLabel.setText("");
        adresseErrorLabel.setTextFill(Color.BLACK);
        telephoneErrorLabel.setText("");
        telephoneErrorLabel.setTextFill(Color.BLACK);
        passwordErrorLabel.setText("");
        passwordErrorLabel.setTextFill(Color.BLACK);
    }
    private boolean checkFieldsNotEmpty() {
        boolean allFieldsFilled = true;
        if (nom.getText().isEmpty()) {
            setErrorLabel(nomErrorLabel, "Nom is required");
            allFieldsFilled = false;
        }

        if (prenom.getText().isEmpty()) {
            setErrorLabel(prenomErrorLabel, "Prénom is required");
            allFieldsFilled = false;
        }
        if (email.getText().isEmpty()) {
            setErrorLabel(emailErrorLabel, "Email is required");
            allFieldsFilled = false;
        }
        if (Adresse.getText().isEmpty()) {
            setErrorLabel(adresseErrorLabel, "Adresse is required");
            allFieldsFilled = false;
        }
        if (telephone.getText().isEmpty()) {
            setErrorLabel(telephoneErrorLabel, "Téléphone is required");
            allFieldsFilled = false;
        }
        if (password.getText().isEmpty()) {
            setErrorLabel(passwordErrorLabel, "Password is required");
            allFieldsFilled = false;
        }


        return allFieldsFilled;
    }

}
