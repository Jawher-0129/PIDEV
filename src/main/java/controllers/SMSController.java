package controllers;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class SMSController {

    // Votre SID de compte Twilio
    public static final String ACCOUNT_SID = "AC8592bdf0c562470c42ea2259cfe488d9";
    // Votre jeton d'authentification Twilio
    public static final String AUTH_TOKEN = "d960d4aa4fbde64fa2ff3e003b8d37c4";

    @FXML
    private TextField phoneNumberField;

    @FXML
    private TextArea messageArea;

    @FXML
    public void envoyerSMS() {
        String destinataire = phoneNumberField.getText();
        System.out.println(destinataire);
        String message = messageArea.getText();

        // Initialiser Twilio
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        // Envoyer le SMS
        Message msg = Message.creator(new PhoneNumber(destinataire), new PhoneNumber("+13345186001"), message).create();

        // Traiter la réponse
        if (msg.getErrorCode() == null) {
            // SMS envoyé avec succès
            System.out.println("SMS envoyé avec succès.");
        } else {
            // Erreur lors de l'envoi du SMS
            System.out.println("Erreur lors de l'envoi du SMS: " + msg.getErrorMessage());
        }
    }
}

