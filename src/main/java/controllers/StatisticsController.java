package controllers;

import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import Entity.Don;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import service.DonService;
import service.EmailService;
import service.StatisticsService;
import org.controlsfx.control.Notifications;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;

public class StatisticsController {
    @FXML
    private BarChart<String, Number> donationsPerCampaignChart;
    @FXML
    private BarChart<String, Number> donationsPerMonthChart;
    @FXML
    private BarChart<String, Number> campaignsPerMonthChart;

    private StatisticsService statisticsService;

    @FXML
    private PieChart donationsWithWithoutCampaignChart;

    private DonService donService; // Declare the DonService
    private EmailService emailService;

    @FXML
    public void initialize() {
        statisticsService = new StatisticsService();
        emailService = new EmailService();
        donService = new DonService(); // Initialize the DonService
        loadCharts();
        loadDonationsWithAndWithoutCampaign(); // Load pie chart data
    }

    @FXML
    private void handleSendEmail() {
        try {
            List<String> donorEmails = statisticsService.retrieveDonorEmails(); // Retrieve donor emails

            String lowDonationCampaigns = statisticsService.countDonationsPerCampaign2().entrySet().stream()
                    .filter(entry -> entry.getValue() < 5) // Assuming <5 donations is considered low
                    .map(Map.Entry::getKey)
                    .collect(Collectors.joining("\n"));

            String[] emails = donorEmails.toArray(new String[0]); // Convert the list to an array
            for (String email : emails) {
                emailService.sendEmail(
                        "tesnim.satouri@esprit.tn",
                        "HealthSwift",
                        email,
                        "Donor",
                        "Campaign Suggestions",
                        "Here are some campaigns that need your support: " + lowDonationCampaigns
                );
            }
            Notifications.create()
                    .title("Email Sent")
                    .text("Your emails have been successfully sent!")
                    .showInformation();
        } catch (MailjetException | MailjetSocketTimeoutException e) {
            Notifications.create()
                    .title("Email Sending Failed")
                    .text("Failed to send emails: " + e.getMessage())
                    .showError();
        }
    }

    @FXML
    private void assignDonations() {
        try {
            Map<String, Integer> donationsPerCampaign = statisticsService.countDonationsPerCampaign();
            String minDonationCampaignId = Collections.min(donationsPerCampaign.entrySet(), Map.Entry.comparingByValue()).getKey();

            List<Don> unassignedDons = donService.findAll().stream().filter(d -> d.getCampagne_id() == null).collect(Collectors.toList());
            unassignedDons.forEach(don -> {
                don.setCampagne_id(Integer.parseInt(minDonationCampaignId));
                donService.update(don);
            });
            updateCharts();

            Notifications.create()
                    .title("Assignment Successful")
                    .text("Donations have been successfully assigned!")
                    .showInformation();
        } catch (Exception e) {
            Notifications.create()
                    .title("Assignment Failed")
                    .text("Failed to assign donations: " + e.getMessage())
                    .showError();
        }
    }


    private void updateCharts() {
        // Clear existing data
        donationsPerCampaignChart.getData().clear();
        donationsPerMonthChart.getData().clear();
        campaignsPerMonthChart.getData().clear();

        // Reload data
        loadDonationsPerCampaign();
        loadDonationsPerMonth();
        loadCampaignsPerMonth();
        loadDonationsWithAndWithoutCampaign(); // Assuming you want to update this chart as well

        // Additional UI updates if necessary, e.g., messages to users
    }



    private void loadDonationsWithAndWithoutCampaign() {
        Map<Boolean, Integer> stats = statisticsService.countDonationsWithWithoutCampaign();
        Platform.runLater(() -> {
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            stats.forEach((associated, count) -> {
                String label = associated ? "With Campaign" : "Without Campaign";
                pieChartData.add(new PieChart.Data(label, count));
            });
            donationsWithWithoutCampaignChart.setData(pieChartData);
        });
    }

    private void loadCharts() {
        loadDonationsPerCampaign();
        loadDonationsPerMonth();
        loadCampaignsPerMonth();
    }

    private void loadDonationsPerCampaign() {
        Map<String, Integer> stats = statisticsService.countDonationsPerCampaign();
        Platform.runLater(() -> {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            Set<String> filteredKeys = new HashSet<>();
            stats.forEach((campaignId, count) -> {
                if (campaignId != null && count != null && filteredKeys.add(campaignId)) {
                    series.getData().add(new XYChart.Data<>(campaignId, count));
                    addTooltipToData(series.getData().get(series.getData().size() - 1));
                }
            });
            donationsPerCampaignChart.getData().clear();
            donationsPerCampaignChart.getData().add(series);
            formatChartAxes(donationsPerCampaignChart);
        });
    }

    private void loadDonationsPerMonth() {
        Map<String, Integer> stats = statisticsService.countDonationsPerMonth();
        Platform.runLater(() -> {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            Set<String> uniqueKeys = new HashSet<>();
            stats.forEach((month, count) -> {
                if (month != null && count != null && uniqueKeys.add(month)) {
                    series.getData().add(new XYChart.Data<>(month, count));
                    addTooltipToData(series.getData().get(series.getData().size() - 1));
                }
            });
            donationsPerMonthChart.getData().clear();
            donationsPerMonthChart.getData().add(series);
            formatChartAxes(donationsPerMonthChart);
        });
    }

    private void loadCampaignsPerMonth() {
        Map<String, Integer> stats = statisticsService.countCampaignsPerMonth();
        Platform.runLater(() -> {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            Set<String> uniqueKeys = new HashSet<>();
            stats.forEach((month, count) -> {
                if (month != null && count != null && uniqueKeys.add(month)) {
                    series.getData().add(new XYChart.Data<>(month, count));
                    addTooltipToData(series.getData().get(series.getData().size() - 1));
                }
            });
            campaignsPerMonthChart.getData().clear();
            campaignsPerMonthChart.getData().add(series);
            formatChartAxes(campaignsPerMonthChart);
        });
    }

    private void formatChartAxes(BarChart<String, Number> chart) {
        CategoryAxis xAxis = (CategoryAxis) chart.getXAxis();
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();

        // Removed the lines that set the axis labels
        // xAxis.setLabel("Categories");
        // yAxis.setLabel("Values");

        xAxis.setTickLabelFont(new Font("Arial", 10));
        xAxis.setTickLabelRotation(-50);

        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(calculateUpperBound(chart));
        yAxis.setTickUnit(calculateTickUnit(chart));
    }

    private double calculateTickUnit(BarChart<String, Number> chart) {
        // Replace with dynamic calculation if needed
        return 1; // For example, each tick represents one unit
    }

    private double calculateUpperBound(BarChart<String, Number> chart) {
        double maxDataValue = chart.getData().stream()
                .flatMap(series -> series.getData().stream())
                .mapToDouble(data -> data.getYValue().doubleValue())
                .max()
                .orElse(0);

        return Math.ceil(maxDataValue); // Round up to the nearest integer if you want integer values
    }

    private void addTooltipToData(XYChart.Data<String, Number> data) {
        Tooltip tooltip = new Tooltip(data.getXValue());
        Tooltip.install(data.getNode(), tooltip);
    }
}
