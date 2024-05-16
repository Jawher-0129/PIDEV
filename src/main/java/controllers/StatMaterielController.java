package controllers;

import Entity.Materiel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import service.MaterielService;

import java.io.IOException;
import java.util.List;


public class StatMaterielController {
    @FXML
    private Button displ;

    @FXML
    PieChart pieChart;

    @FXML
    Button bouttonRetourMateriel;


    @FXML
    void displaystats(ActionEvent event) {

        MaterielService materielService = new MaterielService();
        List<Materiel> materiels = materielService.getAll();

        int disponibleCount = 0;
        int nonDisponibleCount = 0;

        for (Materiel materiel : materiels) {
            if (materiel.getDisponibilite() == 0) {
                nonDisponibleCount++;
            } else {
                disponibleCount++;
            }
        }
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Disponible", disponibleCount),
                        new PieChart.Data("Non Disponible", nonDisponibleCount));
        pieChart.setData(pieChartData);
    }

    @FXML
    void bouttonRetourStatMateriel(ActionEvent event)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gestionressourcesmaterielles/MaterielInterfaceAdmin.fxml"));
            Parent root=loader.load();
            bouttonRetourMateriel.getScene().setRoot(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

