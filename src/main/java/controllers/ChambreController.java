package controllers;

import Entity.Chambre;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;


import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import service.ChambreService;
import service.PersonnelService;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


public class ChambreController {
    private final ChambreService chambreService = new ChambreService();

    @FXML
    private Button AjouterChambreBtn;

    @FXML
    private Label Disponibilite;

    @FXML
    private RadioButton DisponibleRadioButton;

    @FXML
    private TextField LitsDisponible;

    @FXML
    private TextField LitsTotal;
    @FXML
    private ChoiceBox<Integer> personnelChambre;

    @FXML
    private Button ModifierChambreBtn;

    @FXML
    private TextField Numero;

    @FXML
    private TableColumn<Chambre, Integer> NumeroChambre;

    @FXML
    private Button SupprimerChambreBtn;

    @FXML
    private TableColumn<Chambre, Integer> disponibilite;

    @FXML
    private TableColumn<Chambre, Integer> litsDispo;

    @FXML
    private TableColumn<Chambre, Integer> litsTotal;

    @FXML
    private RadioButton nonDisponibleRadioButton;

    @FXML
    private TableColumn<Chambre, Integer> PersonnelChambre;

    @FXML
    private TableView<Chambre> tableChambre;

    private void populateFields(Chambre chambre) {
        this.Numero.setText(Integer.toString(chambre.getNumero()));
        this.personnelChambre.setValue(chambre.getPersonnel());
        this.LitsTotal.setText(Integer.toString(chambre.getNombre_lits_total()));
        this.LitsDisponible.setText(Integer.toString(chambre.getNmbr_lits_disponible()));

        //this.disponibleRadioButton.setSelected(materiel.getDisponibilite() == 1);
        if (chambre.getDisponibilite() == 1) {
            this.DisponibleRadioButton.setSelected(chambre.getDisponibilite() == 1);
        } else
            this.nonDisponibleRadioButton.setSelected(chambre.getDisponibilite() == 0);

        int id_personnel = chambre.getPersonnel();
        ObservableList<Integer> Personnels = personnelChambre.getItems();
        if (Personnels.contains(id_personnel)) {
            personnelChambre.setValue(id_personnel);
        }


    }


    @FXML
    void initialize() {
        configureTableView();

        PersonnelService personnelService = new PersonnelService(); // Create an instance of the PersonnelService class
        List<Integer> id_personnel = personnelService.afficherpersonnel(); // Call the non-static method on the instance


        ObservableList<Integer> idCategoriesList = FXCollections.observableArrayList(id_personnel);
        personnelChambre.setItems(idCategoriesList);

        tableChambre.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

        });
    }

    private void configureTableView() {
        System.out.println("Configuration de la TableView...");
        NumeroChambre.setCellValueFactory(new PropertyValueFactory<>("numero"));
        PersonnelChambre.setCellValueFactory(new PropertyValueFactory<>("personnel"));
        disponibilite.setCellValueFactory(new PropertyValueFactory<>("disponibilite"));
        litsTotal.setCellValueFactory(new PropertyValueFactory<>("nombre_lits_total"));
        litsDispo.setCellValueFactory(new PropertyValueFactory<>("nmbr_lits_disponible"));
        loadChambres();

    }

    private void loadChambres() {

        List<Chambre> chambres = this.chambreService.getAll();
        ObservableList<Chambre> materielObservableList = FXCollections.observableArrayList(chambres);
        this.tableChambre.setItems(materielObservableList);
    }

    @FXML
    void handleAjouterChambre(ActionEvent event) {
        int numero = Integer.parseInt(this.Numero.getText());
        int personnel = this.personnelChambre.getValue();
        int nombre_lits_total = Integer.parseInt(this.LitsTotal.getText());
        int nmbr_lits_disponible = Integer.parseInt(this.LitsDisponible.getText());


        int disponibilite;

        if (nonDisponibleRadioButton.isSelected()) {
            disponibilite = 0;
        } else {
            disponibilite = 1;
        }


        if (numero < 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Erreur : Veuillez entrer un numero positif");
            alert.show();
            return;
        }


        Chambre newChambre = new Chambre(numero, personnel, disponibilite, nombre_lits_total, nmbr_lits_disponible);
        this.chambreService.add(newChambre);

        loadChambres();
    }

    @FXML
    void handleModifierChambre(ActionEvent event) {
        Chambre selectedMateriel = (Chambre) this.tableChambre.getSelectionModel().getSelectedItem();
        if (selectedMateriel != null) {
            String numeroText = this.Numero.getText();
            String litsTotalText = this.LitsTotal.getText();
            String litsDisponibleText = this.LitsDisponible.getText();

            // Vérification que les champs ne sont pas vides et contiennent des valeurs numériques valides
            if (isNumeric(numeroText) && isNumeric(litsTotalText) && isNumeric(litsDisponibleText)) {
                int numero = Integer.parseInt(numeroText);
                int personnel = this.personnelChambre.getValue();
                int nombre_lits_total = Integer.parseInt(litsTotalText);
                int nmbr_lits_disponible = Integer.parseInt(litsDisponibleText);

                int disponibilite;
                if (nonDisponibleRadioButton.isSelected()) {
                    disponibilite = 0;
                } else {
                    disponibilite = 1;
                }

                Chambre newPer = new Chambre(numero, personnel, disponibilite, nombre_lits_total, nmbr_lits_disponible);
                this.chambreService.update(newPer, selectedMateriel.getNumero());
                loadChambres();
// Afficher une alerte de succès
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Chambre mise à jour", "La chambre a été mise à jour avec succès.");
            } else {
                // Afficher une alerte d'erreur si les valeurs ne sont pas numériques
                showAlert(Alert.AlertType.ERROR, "Erreur", "Valeurs incorrectes", "Veuillez saisir des valeurs numériques valides.");
            }
        } else {
            // Afficher une alerte d'erreur si aucun élément n'est sélectionné dans la table
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune chambre sélectionnée", "Veuillez sélectionner une chambre à mettre à jour.");
        }
    }

    // Méthode pour vérifier si une chaîne est numérique
    private boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }



    @FXML
    void handleSupprimerChambre (ActionEvent event){
        Chambre selectedMateriel = (Chambre) this.tableChambre.getSelectionModel().getSelectedItem();
        if (selectedMateriel != null) {
            this.chambreService.delete(selectedMateriel.getNumero());
            System.out.println("Suppression effectuée");
            this.loadChambres();
        }
    }
}

