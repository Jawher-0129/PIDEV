package controllers;

import service.EvenementService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import service.TwilioSmsSender;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Label totalEventsLabel;

    @FXML
    private Label availableEventsLabel;

    @FXML
    private BarChart<String, Integer> eventsByDateChart;

    @FXML
    private LineChart<String, Integer> eventsByDurationChart;

    private EvenementService evenementService = new EvenementService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateTotalEvents();
        updateAvailableEvents();
        updateEventsByDateChart();
        updateEventsByDurationChart();
        checkEventsAndSendSms();

    }

    private void updateTotalEvents() {
        int totalEvents = evenementService.getTotalEvents();
        totalEventsLabel.setText(String.valueOf(totalEvents));
    }

    private void updateAvailableEvents() {
        int availableEvents = evenementService.getAvailableEvents();
        availableEventsLabel.setText(String.valueOf(availableEvents));
    }

    private void updateEventsByDateChart() {
        List<String> dates = evenementService.getDistinctDatesOfEvents();
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        for (String date : dates) {
            int count = evenementService.getEventCountByDate(date);
            XYChart.Data<String, Integer> data = new XYChart.Data<>(date, count);

            // Check if there are more than one event for this date
            if (count > 1) {
                data.nodeProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        newValue.setStyle("-fx-bar-fill: red;"); // Change the color of the bar to red
                    }
                });
            }

            series.getData().add(data);
        }
        eventsByDateChart.getData().add(series);
    }


    private void updateEventsByDurationChart() {
        List<Integer> durations = evenementService.getDistinctDurationsOfEvents();
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        for (Integer duration : durations) {
            int count = evenementService.getEventCountByDuration(duration);
            series.getData().add(new XYChart.Data<>(String.valueOf(duration), count));
        }
        eventsByDurationChart.getData().add(series);
    }
    private void checkEventsAndSendSms() {
        List<String> dates = evenementService.getDistinctDatesOfEvents();
        StringBuilder messageBuilder = new StringBuilder();
        boolean multipleEventsFound = false;

        for (String date : dates) {
            int count = evenementService.getEventCountByDate(date);
            if (count > 1) {
                if (!multipleEventsFound) {
                    messageBuilder.append("Multiple events are scheduled for the following dates:\n");
                    multipleEventsFound = true;
                }
                messageBuilder.append("- ").append(date).append(": ");
                List<String> eventNames = evenementService.getEventNamesByDate(date);
                for (String eventName : eventNames) {
                    messageBuilder.append(eventName).append(", ");
                }
                // Remove the last comma and space
                messageBuilder.delete(messageBuilder.length() - 2, messageBuilder.length());
                messageBuilder.append("\n");
            }
        }

        if (multipleEventsFound) {
            String message = messageBuilder.toString();
            TwilioSmsSender.sendSms("+21656689183", message);
        }
    }




}

