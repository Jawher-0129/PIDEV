package controllers;

import Entity.Evenement;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import java.util.List;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import service.EvenementService;
import service.MailUtility;
import javafx.scene.control.Alert;


public class EvenementFront implements Initializable {

    @FXML
    private TilePane cardsContainer;

    @FXML
    private HBox latestEventsBox;

    @FXML
    private ScrollPane latestEventsScrollPane;



    private EvenementService evenementService = new EvenementService();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadEvenements();
        loadLatestEvenements();
        animateLatestEvents();
    }

    private void loadEvenements() {
        for (Evenement evenement : evenementService.getAll()) {
            VBox card = createEvenementCard(evenement);
            cardsContainer.getChildren().add(card);

        }
    }

    private VBox createEvenementCard(Evenement evenement) {
        VBox card = new VBox(10);
        card.getStyleClass().add("evenement-card");

        // Display image
        try {
            String imageurl;
            if(!(evenement.getImage().startsWith("C")))
            {
                imageurl="C:\\Users\\jawhe\\OneDrive\\Bureau\\3A32HealthSwiftIntegration (2)\\3A32HealthSwiftIntegration\\3A32HealthSwift\\public\\uploads\\Images\\"+evenement.getImage();
            }
            else
                imageurl=evenement.getImage();
            ImageView imageView = new ImageView(new Image(new FileInputStream(imageurl)));
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            card.getChildren().add(imageView);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Display title
        Label titleLabel = new Label(evenement.getTitre());
        titleLabel.getStyleClass().add("evenement-title");
        titleLabel.setWrapText(true); // Set wrapText to true
        titleLabel.setMaxWidth(Double.MAX_VALUE); // Set maximum width to accommodate full text
        card.getChildren().add(titleLabel);

        // Display date
        Label dateLabel = new Label("Date: " + evenement.getDate());
        dateLabel.getStyleClass().add("evenement-date");
        card.getChildren().add(dateLabel);

        // Display lieu
        Label lieuLabel = new Label("Lieu: " + evenement.getLieu());
        lieuLabel.getStyleClass().add("evenement-lieu");
        card.getChildren().add(lieuLabel);

        // Add double-click event handler
        card.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                // Show full details in popup
                showFullDetailsPopup(evenement);
            }
        });

        return card;
    }





    private void showFullDetailsPopup(Evenement evenement) {
        // Convert the event date to a string representation
        String eventDate = new SimpleDateFormat("yyyy-MM-dd").format(evenement.getDate());

        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        try {
            String imageurl;
            if(!(evenement.getImage().startsWith("C")))
            {
                imageurl="C:\\Users\\jawhe\\OneDrive\\Bureau\\3A32HealthSwiftIntegration (2)\\3A32HealthSwiftIntegration\\3A32HealthSwift\\public\\uploads\\Images\\"+evenement.getImage();
            }
            else
                imageurl=evenement.getImage();

            ImageView imageView = new ImageView(new Image(new FileInputStream(imageurl)));
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            HBox imageBox = new HBox(imageView);
            imageBox.setAlignment(Pos.CENTER);
            vbox.getChildren().add(imageBox);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Label titleLabel = new Label("Title: " + evenement.getTitre());
        Label dateLabel = new Label("Date: " + eventDate); // Use the string representation of the date
        Label dureeLabel = new Label("Duration: " + evenement.getDuree());
        Label lieuLabel = new Label("Location: " + evenement.getLieu());
        Label objectifLabel = new Label("Objective: " + evenement.getObjectif());

        vbox.getChildren().addAll(titleLabel, dateLabel, dureeLabel, lieuLabel, objectifLabel);

        Button feedbackButton = new Button("Feedback");
        Button participationButton = new Button("Participation");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(feedbackButton, participationButton);
        vbox.getChildren().add(buttonBox);

        LocalDate currentDate = LocalDate.now();
        LocalDate parsedEventDate = LocalDate.parse(eventDate);
        boolean isEventPassed = parsedEventDate.isBefore(currentDate);

        if (isEventPassed) {
            feedbackButton.setDisable(false);
            participationButton.setDisable(true);
        } else {
            feedbackButton.setDisable(true);
            participationButton.setDisable(false);
        }

        feedbackButton.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Provide Feedback");
            dialog.setHeaderText("Enter Your Feedback");
            dialog.setContentText("Please provide your feedback:");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String feedback = result.get();
                String feedbackSubject = "Feedback for Event: " + evenement.getTitre();
                String feedbackBody = "Dear Malika,\n\nFeedback for the event '" + evenement.getTitre() + "':\n" + feedback;
                // Use Mailtrap SMTP server settings
                MailUtility.sendEmail("93d152882a5ae8", "9577789f70f4e8", "sandbox.smtp.mailtrap.io", "2525", "malika.gharbi@esprit.tn", feedbackSubject, feedbackBody);

                // Optionally, you can display a confirmation message to the user
                Alert confirmation = new Alert(Alert.AlertType.INFORMATION);
                confirmation.setTitle("Feedback Sent");
                confirmation.setHeaderText(null);
                confirmation.setContentText("Thank you for your feedback!");
                confirmation.showAndWait();
            }
        });


        participationButton.setOnAction(event -> {
            registerForParticipation(evenement);
        });

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Event Details");

        Scene scene = new Scene(vbox, 500, 400);
        popupStage.setScene(scene);

        popupStage.show();
    }


    private void loadLatestEvenements() {
        if (latestEventsBox != null) {
            List<Evenement> latestEvents = evenementService.getLatestEvents(10); // Get the latest 5 events
            for (Evenement evenement : latestEvents) {
                VBox card = createSimpleEventCard(evenement); // Create a simple event card
                latestEventsBox.getChildren().add(card);
            }
        } else {
            System.out.println("latestEventsBox is null");
        }
    }

    private VBox createSimpleEventCard(Evenement evenement) {
        VBox card = new VBox(10);
        card.getStyleClass().add("evenement-card");

        // Display title
        Label titleLabel = new Label(evenement.getTitre());
        titleLabel.getStyleClass().add("evenement-title");
        titleLabel.setWrapText(true); // Set wrapText to true
        titleLabel.setMaxWidth(Double.MAX_VALUE); // Set maximum width to accommodate full text
        card.getChildren().add(titleLabel);

        // Display date
        Label dateLabel = new Label("Date: " + evenement.getDate());
        dateLabel.getStyleClass().add("evenement-date");
        card.getChildren().add(dateLabel);

        // Display lieu
        Label lieuLabel = new Label("Lieu: " + evenement.getLieu());
        lieuLabel.getStyleClass().add("evenement-lieu");
        card.getChildren().add(lieuLabel);

        // Add double-click event handler
        card.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                // Show full details in popup
                showFullDetailsPopup(evenement);
            }
        });

        return card;
    }
    private void animateLatestEvents() {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(10), latestEventsBox);
        transition.setToX(-200);
        transition.setCycleCount(TranslateTransition.INDEFINITE);
        transition.play();
    }
    private boolean isValidEmailAddress(String email) {
        // Implement email validation logic here (e.g., using regular expressions)
        // For simplicity, you can use a basic check
        return email != null && email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    }
    private void registerForParticipation(Evenement evenement) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Participation Confirmation");
        dialog.setHeaderText("Enter Your Email");
        dialog.setContentText("Please enter your email address:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String participantEmail = result.get();

            if (isValidEmailAddress(participantEmail)) {
                String confirmationSubject = "Participation Confirmation: " + evenement.getTitre();
                String confirmationBody = "Dear Participant,\n\nCongratulations! You have successfully registered for the event.";
                MailUtility.sendEmail(participantEmail, confirmationSubject, confirmationBody);

                // Optionally, you can display a message or perform other actions after sending the confirmation email
                Alert confirmationAlert = new Alert(Alert.AlertType.INFORMATION);
                confirmationAlert.setTitle("Confirmation Sent");
                confirmationAlert.setHeaderText(null);
                confirmationAlert.setContentText("Confirmation email sent to " + participantEmail);
                confirmationAlert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Email");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a valid email address.");
                alert.showAndWait();
            }
        }
    }
}

