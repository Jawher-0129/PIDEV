package service;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EmailService {

    private MailjetClient client;

    public EmailService() {
        Properties props = loadMailjetConfig();
        this.client = new MailjetClient(
                props.getProperty("mailjet.apikey.public"),
                props.getProperty("mailjet.apikey.private"),
                new ClientOptions("v3.1")
        );
    }

    private Properties loadMailjetConfig() {
        Properties prop = new Properties();
        try (InputStream input = EmailService.class.getClassLoader().getResourceAsStream("mailjet_config.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find mailjet_config.properties");
            }
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error loading Mailjet configuration", ex);
        }
        return prop;
    }

    public void sendDonationConfirmationEmail(String recipientEmail, String recipientName) throws MailjetException, MailjetSocketTimeoutException {
        JSONArray recipients = new JSONArray().put(new JSONObject().put("Email", recipientEmail).put("Name", recipientName));
        JSONObject message = new JSONObject()
                .put(Emailv31.Message.FROM, new JSONObject().put("Email", "tesnim.satouri@esprit.tn").put("Name", "Tesnim Satouri"))
                .put(Emailv31.Message.TO, recipients)
                .put(Emailv31.Message.TEMPLATEID, 5750978)
                .put(Emailv31.Message.TEMPLATELANGUAGE, true)
                .put(Emailv31.Message.SUBJECT, "Confirmation de votre don")
                .put(Emailv31.Message.VARIABLES, new JSONObject().put("content", "Merci pour votre générosité ! Votre don a été ajouté avec succès."));

        MailjetRequest request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray().put(message));

        MailjetResponse response = this.client.post(request);
        System.out.println("Email sent. Status: " + response.getStatus());
        System.out.println("Response Data: " + response.getData().toString());
    }

    // General email sending method for other use cases
    public void sendEmail(String fromEmail, String fromName, String toEmail, String toName, String subject, String htmlContent) throws MailjetException, MailjetSocketTimeoutException {
        JSONArray recipients = new JSONArray().put(new JSONObject().put("Email", toEmail).put("Name", toName));
        JSONObject message = new JSONObject()
                .put(Emailv31.Message.FROM, new JSONObject().put("Email", fromEmail).put("Name", fromName))
                .put(Emailv31.Message.TO, recipients)
                .put(Emailv31.Message.SUBJECT, subject)
                .put(Emailv31.Message.HTMLPART, htmlContent);

        MailjetRequest request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray().put(message));

        MailjetResponse response = this.client.post(request);
        System.out.println("Response Status: " + response.getStatus());
        System.out.println("Response Data: " + response.getData().toString());
    }
        public void sendEmail(String recipient, String subject, String body) {
            System.out.println("E-mail envoyé à " + recipient);
            System.out.println("Objet : " + subject);
            System.out.println("Corps de l'e-mail : " + body);



    }


}
