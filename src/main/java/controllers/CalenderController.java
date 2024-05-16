package controllers;


import Entity.RendezVous;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import service.RendezVousService;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CalenderController implements Initializable {
    ZonedDateTime dateFocus;
    ZonedDateTime today;
    @FXML
    private Button annuler;
    @FXML
    private Label idRENDEZV;
    @FXML
    private FlowPane calendar;
    @FXML
    private ChoiceBox<String> heure;
    @FXML
    private DatePicker date;

    @FXML
    private TextField lieu;

    @FXML
    private Label idDemande;


    @FXML
    private Button modifier;
    @FXML
    private TextArea objective;

    @FXML
    private Text month;
    private RendezVousService rendezVousService;

    public CalenderController() {
        rendezVousService = new RendezVousService();
    }

    @FXML
    private Text year;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dateFocus = ZonedDateTime.now();
        today = ZonedDateTime.now();
        drawCalendar();
       for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute += 15) {
                String formattedHour = String.format("%02d", hour); // Ajoute un zéro en tête pour les heures < 10
                String formattedMinute = String.format("%02d", minute); // Ajoute un zéro en tête pour les minutes < 10
                heure.getItems().add(formattedHour + ":" + formattedMinute);
            }
        }
    }

    @FXML
    void backOneMonth(ActionEvent event) {
        dateFocus = dateFocus.minusMonths(1);
        calendar.getChildren().clear();
        drawCalendar();
    }

    @FXML
    void forwardOneMonth(ActionEvent event) {
        dateFocus = dateFocus.plusMonths(1);
        calendar.getChildren().clear();
        drawCalendar();
    }

    private void drawCalendar() {
        year.setText(String.valueOf(dateFocus.getYear()));
        month.setText(String.valueOf(dateFocus.getMonth()));
       // calendar.setVgap(0);
       // calendar.setHgap(0);

        double calendarWidth = calendar.getPrefWidth();
        double calendarHeight = calendar.getPrefHeight();
        double strokeWidth = 1;
        double spacingH = calendar.getHgap();
        double spacingV = calendar.getVgap();

        // Créer le rendezVousMap en utilisant la méthode createCalendarMap()
        Map<LocalDate, List<RendezVous>> rendezVousMap = createCalendarMap();

        int monthMaxDate = dateFocus.getMonth().maxLength();
        // Check for leap year
        if (dateFocus.getYear() % 4 != 0 && monthMaxDate == 29) {
            monthMaxDate = 28;
        }
        int dateOffset = dateFocus.withDayOfMonth(1).getDayOfWeek().getValue();

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                StackPane stackPane = new StackPane();

                Rectangle rectangle = new Rectangle();
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setStroke(Color.BLACK);
                rectangle.setStrokeWidth(strokeWidth);
                double rectangleWidth = (calendarWidth / 7) - strokeWidth - spacingH;
                rectangle.setWidth(rectangleWidth);
                double rectangleHeight = (calendarHeight / 6) - strokeWidth - spacingV;
                rectangle.setHeight(rectangleHeight);
                stackPane.getChildren().add(rectangle);

                int calculatedDate = (j + 1) + (7 * i);
                if (calculatedDate > dateOffset) {
                    int currentDate = calculatedDate - dateOffset;
                    if (currentDate <= monthMaxDate) {
                        Text date = new Text(String.valueOf(currentDate));
                        double textTranslationY = -(rectangleHeight / 2) * 0.75;
                        date.setTranslateY(textTranslationY);
                        stackPane.getChildren().add(date);

                        LocalDate currentDateLocalDate = LocalDate.of(dateFocus.getYear(), dateFocus.getMonth(), currentDate);
                        List<RendezVous> rendezVousList = rendezVousMap.get(currentDateLocalDate);
                        if (rendezVousList != null) {
                            createCalendarActivity(rendezVousList, rectangleHeight, rectangleWidth, stackPane);
                        }
                    }
                    if (today.getYear() == dateFocus.getYear() && today.getMonth() == dateFocus.getMonth() && today.getDayOfMonth() == currentDate) {
                        rectangle.setStroke(Color.BLUE);
                    }
                }
                calendar.getChildren().add(stackPane);
            }
        }
    }

    private void createCalendarActivity(List<RendezVous> rendezVousList, double rectangleHeight, double rectangleWidth, StackPane stackPane) {
        for (int k = 0; k < rendezVousList.size(); k++) {
            if (k >= 2) {
                Text moreActivities = new Text("...");
                stackPane.getChildren().add(moreActivities);
                moreActivities.setOnMouseClicked(mouseEvent -> {
                    // Sur le clic de "..."
                    System.out.println(rendezVousList); // Affiche tous les rendez-vous pour la date donnée
                });
                break;
            }

            RendezVous rendezVous = rendezVousList.get(k);
            Timestamp timestamp = rendezVous.getDate();

            LocalDateTime localDateTime = timestamp.toLocalDateTime();
            LocalDate rendezVousDate = localDateTime.toLocalDate();
            LocalTime rendezVousTime = localDateTime.toLocalTime();

            Rectangle activityRectangle = new Rectangle();
            activityRectangle.setFill(Color.rgb(255, 153, 153));
            activityRectangle.setWidth(rectangleWidth);
            double activityRectangleHeight = rectangleHeight / 2;
            activityRectangle.setHeight(activityRectangleHeight);
            stackPane.getChildren().add(activityRectangle);

            Text activityTime = new Text(rendezVousTime.format(DateTimeFormatter.ofPattern("HH:mm")));
            double textTranslationY = -(activityRectangleHeight / 2) * 0.75;
            activityTime.setTranslateY(textTranslationY);
            stackPane.getChildren().add(activityTime);
            String directeurCampagne = rendezVousService.getDirecteurCampagne(rendezVous.getDemande());

            // Afficher le directeur de campagne
            Text directeurCampagneText = new Text(directeurCampagne);
            directeurCampagneText.setTranslateY(textTranslationY + 20); // Ajuster la position verticale si nécessaire
            stackPane.getChildren().add(directeurCampagneText);
            activityRectangle.setOnMouseClicked(mouseEvent -> {
                System.out.println(rendezVous); // Affiche le rendez-vous spécifique
                idRENDEZV.setText(String.valueOf(rendezVous.getId_rendezvous()));
                lieu.setText(rendezVous.getLieu());
                objective.setText(rendezVous.getObjective());
                Timestamp ts = rendezVous.getDate();
                LocalDateTime lD = ts.toLocalDateTime();
                LocalDate localDate = lD.toLocalDate();
                date.setValue(localDate);
                String heureFormattee = localDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
                heure.setValue(heureFormattee);
               // idDemande.setVisible(false);
               idDemande.setText(String.valueOf(rendezVous.getDemande()));
            });
        }
    }

    private Map<LocalDate, List<RendezVous>> createCalendarMap() {
        List<RendezVous> rendezVousList = rendezVousService.getAll();
        Map<LocalDate, List<RendezVous>> rendezVousMap = new HashMap<>();

        for (RendezVous rendezVous : rendezVousList) {
            Timestamp rendezVousTimestamp = rendezVous.getDate();
            LocalDateTime rendezVousDateTime = rendezVousTimestamp.toLocalDateTime();
            LocalDate rendezVousDate = rendezVousDateTime.toLocalDate();

            List<RendezVous> dayRendezVousList = rendezVousMap.getOrDefault(rendezVousDate, new ArrayList<>());
            dayRendezVousList.add(rendezVous);
            rendezVousMap.put(rendezVousDate, dayRendezVousList);
        }

        return rendezVousMap;
    }
    @FXML
    protected void handleUpdateRendezV() {
        String idRENDEZVtext = idRENDEZV.getText();
        int IdRendezvous = Integer.parseInt(idRENDEZVtext);
        String idDemandetext = idDemande.getText();
        int IdDemande = Integer.parseInt(idDemandetext);
        String lieuText = lieu.getText(); // Récupère la description depuis la zone de texte
        String objectiveText = objective.getText();
        LocalDate localDate = date.getValue(); // Récupère la date depuis le DatePicker
        String selectedHour = heure.getValue();
        LocalTime localTime = LocalTime.parse(selectedHour); // Convertis l'heure sélectionnée en LocalTime
        LocalDateTime dateTime = LocalDateTime.of(localDate, localTime); // Combine la date et l'heure
        Timestamp timestamp = Timestamp.valueOf(dateTime);
        RendezVous nouvelleR = new RendezVous(timestamp, lieuText, objectiveText,IdDemande);
        rendezVousService.update(nouvelleR,IdRendezvous);
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Rendez-vous mis à jour avec succès.");
        dateFocus = ZonedDateTime.now();
        today = ZonedDateTime.now();
        drawCalendar();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}