package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import service.PersonnelService;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class PersonnelStaticsController implements Initializable {

    @FXML
    private PieChart roleStatisticsPieChart;

    @FXML
    private PieChart experienceStatisticsPieChart;

    private PersonnelService personnelService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the personnel service
        personnelService = new PersonnelService();

        // Display the statistics
        displayRoleStatistics();
        displayExperienceStatistics();
    }

    private void displayRoleStatistics() {
        // Retrieve statistics by role from the personnel service
        Map<String, Integer> personnelByRole = personnelService.countPersonnelByRole();

        // Clear previous data
        roleStatisticsPieChart.getData().clear();

        // Add statistics to the pie chart for roles
        for (Map.Entry<String, Integer> entry : personnelByRole.entrySet()) {
            roleStatisticsPieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
    }

    private void displayExperienceStatistics() {
        // Retrieve statistics by experience from the personnel service
        Map<Integer, Integer> personnelByExperience = personnelService.countPersonnelByExperience();

        // Clear previous data
        experienceStatisticsPieChart.getData().clear();

        // Add statistics to the pie chart for experience
        for (Map.Entry<Integer, Integer> entry : personnelByExperience.entrySet()) {
            experienceStatisticsPieChart.getData().add(new PieChart.Data("Experience " + entry.getKey() + " ans", entry.getValue()));
        }
    }
}
