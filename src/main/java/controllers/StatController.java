package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;
import service.Usercrud;
import Entity.User;

import java.util.List;

public class StatController {

    @FXML
    PieChart pieChart;

    @FXML
    void displaystats(ActionEvent event) {
        // Récupérer tous les utilisateurs
        Usercrud userService = new Usercrud();
        List<User> users = userService.getAllData();

        // Initialiser les compteurs pour chaque rôle
        int donateurCount = 0;
        int directeurCount = 0;

        // Compter le nombre d'utilisateurs pour chaque rôle
        for (User user : users) {
            String role = user.getRoles();
            if ("[\"ROLE_DONATEUR\"]".equals(role)) {
                donateurCount++;
            } else if ("[\"ROLE_DIRECTEUR\"]".equals(role)) {
                directeurCount++;
            }
        }

        // Calculer les pourcentages pour chaque rôle
        double totalUsers = donateurCount + directeurCount;
        double donateurPercentage = (donateurCount / totalUsers) * 100;
        double directeurPercentage = (directeurCount / totalUsers) * 100;

        // Créer des libellés personnalisés avec les pourcentages
        String donateurLabel = String.format("Donateur (%.2f%%)", donateurPercentage);
        String directeurLabel = String.format("Directeur de campagne (%.2f%%)", directeurPercentage);

        // Créer les données pour le PieChart avec les libellés personnalisés
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data(donateurLabel, donateurCount),
                        new PieChart.Data(directeurLabel, directeurCount));

        // Définir les données sur le PieChart
        pieChart.setData(pieChartData);

        // Définir la taille du PieChart
        pieChart.setPrefSize(600, 600); // Ajustez la taille selon vos besoins
    }

    private void switchScene(String fxmlFile, ActionEvent event) {
        try {
            System.out.println("fxml:"+ fxmlFile);

            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void back(ActionEvent event) {
        switchScene("/AdminPage.fxml", event);

    }
}
