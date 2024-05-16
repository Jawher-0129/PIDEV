package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import service.EmailService;

public class EmailController {
    @FXML
    private TextField recipientField;

    @FXML
    private TextField subjectField;

    @FXML
    private TextArea contentArea;

    private EmailService emailService;

    public EmailController() {
        this.emailService = new EmailService();
    }

    @FXML
    private void sendEmail() {
        String recipient = recipientField.getText();
        String subject = subjectField.getText();
        String body = contentArea.getText();

        // Envoi de l'e-mail
        emailService.sendEmail(recipient, subject, body);

        // Affichage d'un message de confirmation ou de succès
    }
}
