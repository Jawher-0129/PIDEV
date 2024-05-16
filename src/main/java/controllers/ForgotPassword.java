package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.scene.input.KeyEvent;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.Usercrud;

public class ForgotPassword {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField emailField;

    @FXML
    private TextField box1;

    @FXML
    private TextField box2;

    @FXML
    private TextField box3;

    @FXML
    private TextField box4;

    @FXML
    private TextField box5;

    @FXML
    private TextField box6;

    private void switchScene(String fxmlFile, String email, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Pass the email to the ResetPassword controller
            ResetPassword resetPasswordController = loader.getController();
            resetPasswordController.setEmail(email);

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void resetPassword(ActionEvent event) {
        String email = emailField.getText();
        String verificationCode = box1.getText() + box2.getText() + box3.getText() + box4.getText() + box5.getText() + box6.getText();

        // Check if the verification code is correct
        Usercrud y = new Usercrud();
        if (y.isVerificationCodeCorrect(email, verificationCode)) {
            // Redirect the user to the ResetPassword interface and pass the email
            switchScene("/resetPassword.fxml", email, event);

            System.out.println("Verification code is correct. Redirecting to reset password interface.");
        } else {
            // Display an error message if the verification code is incorrect
            showAlert(Alert.AlertType.ERROR, "Error", "Incorrect verification code. Please try again.");
        }

    }

    @FXML
    void sendVerificationCode(ActionEvent event) {
        String email = emailField.getText();

        // Check if the email is empty
        if (email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter your email address.");
            return;
        }

        // Check if the email is valid
        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid email address.");
            return;
        }

        // Check if the email exists in the database
        Usercrud Usercr = new Usercrud();
        if (!Usercr.isEmailExistsInDatabase(email)) {
            showAlert(Alert.AlertType.ERROR, "Error", "The entered email does not exist.");
            return;
        }
        // Send the verification code via email using SendGrid API
        sendVerificationCodeViaSendGrid(email);
    }

    private void sendVerificationCodeViaSendGrid(String email) {
        Email from = new Email("firaszighni90@gmail.com");
        String subject = "Verification Code";
        Email to = new Email(email);
        String verificationCode = generateVerificationCode();
        Usercrud x = new Usercrud();
        x.updateVerificationCode(email, verificationCode);
        String bodyContent = "Your verification code is: " + verificationCode;
        Content content = new Content("text/plain", bodyContent);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid("");
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Verification code sent to your email.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to send verification code. Please try again.");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to send verification code. Please try again later.");
        }
    }

    private String generateVerificationCode() {
        // Define the length of the verification code
        int length = 6;

        // Define the characters allowed in the verification code
        String characters = "0123456789";

        // Create a StringBuilder to store the verification code
        StringBuilder verificationCode = new StringBuilder();

        // Create a random object
        Random random = new Random();

        // Generate the verification code
        for (int i = 0; i < length; i++) {
            // Generate a random index within the range of the characters string
            int index = random.nextInt(characters.length());

            // Append the character at the random index to the verification code
            verificationCode.append(characters.charAt(index));
        }

        // Convert the StringBuilder to a String and return the verification code
        return verificationCode.toString();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isValidEmail(String email) {
        // Your email validation logic here
        return email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    }

    @FXML
    void initialize() {
        assert emailField != null : "fx:id=\"emailField\" was not injected: check your FXML file 'ForgotPassword.fxml'.";
        assert box1 != null : "fx:id=\"box1\" was not injected: check your FXML file 'ForgotPassword.fxml'.";
        assert box2 != null : "fx:id=\"box2\" was not injected: check your FXML file 'ForgotPassword.fxml'.";
        assert box3 != null : "fx:id=\"box3\" was not injected: check your FXML file 'ForgotPassword.fxml'.";
        assert box4 != null : "fx:id=\"box4\" was not injected: check your FXML file 'ForgotPassword.fxml'.";
        assert box5 != null : "fx:id=\"box5\" was not injected: check your FXML file 'ForgotPassword.fxml'.";
        assert box6 != null : "fx:id=\"box6\" was not injected: check your FXML file 'ForgotPassword.fxml'.";
        limitTextFieldLength(box1);
        limitTextFieldLength(box2);
        limitTextFieldLength(box3);
        limitTextFieldLength(box4);
        limitTextFieldLength(box5);
        limitTextFieldLength(box6);

        addTextFieldListener(box1, box2);
        addTextFieldListener(box2, box3);
        addTextFieldListener(box3, box4);
        addTextFieldListener(box4, box5);
        addTextFieldListener(box5, box6);
    }
    private void limitTextFieldLength(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 1) {
                textField.setText(newValue.substring(0, 1));
            }
        });
    }
    private void addTextFieldListener(TextField currentBox, TextField nextBox) {
        currentBox.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (!event.getText().matches("[0-9]")) {
                currentBox.setText("");
                return;
            }

            if (!currentBox.getText().isEmpty()) {
                nextBox.requestFocus();
            }
        });
    }
}