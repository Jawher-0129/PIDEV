package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.JOptionPane;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.poi.ss.usermodel.Cell;
import org.controlsfx.control.Notifications;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
public class ControllerSMS {
    @FXML
    private Button sendSMS;

    @FXML
    private TextField txtapi;

    @FXML
    private TextArea txtmessage;

    @FXML
    private TextField txtnumber;

    @FXML
    private TextField txtsender;
    @FXML
    private void initialize() {
        List<List<String>> data = new ArrayList<>();
        data.add(Arrays.asList("Nom", "Âge", "Pays"));
        data.add(Arrays.asList("John Doe", "30", "États-Unis"));
        data.add(Arrays.asList("Jane Smith", "25", "Canada"));

        Button exportButton = new Button("Exporter vers Excel");
        exportButton.setOnAction(event -> {
            String filePath = "export.xlsx";
            exportToExcel(data, filePath);
            System.out.println("Données exportées vers Excel.");
        });
    }
    private void exportToExcel(List<List<String>> data, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Data");

            int rowNum = 0;
            for (List<String> rowData : data) {
                Row row = sheet.createRow(rowNum++);
                int colNum = 0;
                for (String cellData : rowData) {
                    Cell cell = row.createCell(colNum++);
                    cell.setCellValue(cellData);
                }
            }

            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void SendSMS() {
         String ACCOUNT_SID = "AC8bd6a0a5b22855c243dc60296543bf84";
         String AUTH_TOKEN = "c49c523215ae420d2cc398261c5a1b1b";


            // Initialisez Twilio avec vos identifiants
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

            // Définissez le numéro de téléphone Twilio à partir duquel vous souhaitez envoyer le SMS
            String twilioPhoneNumber = "+19149086373";

            // Numéro de téléphone de destination
            String recipientPhoneNumber = "+21628749555"; // Remplacez par le numéro de téléphone du destinataire

            // Message à envoyer
            String messageBody = "Votre message ici";

            // Envoyer le message
            Message message = Message.creator(
                            new PhoneNumber(recipientPhoneNumber),
                            new PhoneNumber(twilioPhoneNumber),
                            messageBody)
                    .create();

            // Afficher le SID du message s'il est envoyé avec succès
            System.out.println("Message SID: " + message.getSid());
            showNotification("message envoyer");
        String notificationText = "La demande a été mise à jour avec succès.";
        String notificationTitle = "Mise à jour de la demande";
        Button showNotificationButton = new Button("Afficher la notification");
        showNotificationButton.setOnAction(e -> showNotification());

        VBox root = new VBox(10, showNotificationButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));

        ////


    }
    @FXML
    private void showNotification() {
        Notifications.create()
                .title("Notification")
                .text("Ceci est une notification !")
                .position(Pos.TOP_RIGHT)
                .hideAfter(Duration.seconds(5))
                .show();
        List<List<String>> data = new ArrayList<>();
        data.add(Arrays.asList("Nom", "Âge", "Pays"));
        data.add(Arrays.asList("John Doe", "30", "États-Unis"));
        data.add(Arrays.asList("Jane Smith", "25", "Canada"));


        String filePath = "export.xlsx";
        exportToExcel(data, filePath);
        System.out.println("Données exportées vers Excel.");

    }

    private void showNotification(String message) {
        // Créer une étiquette pour afficher le message de notification
        Label notificationLabel = new Label(message);
        notificationLabel.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-padding: 10px;");
        notificationLabel.setAlignment(Pos.CENTER);
        notificationLabel.setWrapText(true);

        // Empiler l'étiquette dans un conteneur StackPane pour la centrer
        StackPane notificationPane = new StackPane(notificationLabel);
        notificationPane.setAlignment(Pos.CENTER);

        // Créer une nouvelle scène pour afficher la notification
        Scene notificationScene = new Scene(notificationPane, 300, 100);
        Stage notificationStage = new Stage();
        notificationStage.setScene(notificationScene);
        notificationStage.setTitle("Notification");
        notificationStage.show();

        // Définir une animation pour faire disparaître la notification après quelques secondes
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(20),new KeyValue(notificationStage.opacityProperty(), 0))
        );
        timeline.setOnFinished(event -> notificationStage.close());
        timeline.play();
    }

}