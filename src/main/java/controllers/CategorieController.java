package controllers;
import Entity.Categorie;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import service.CategorieService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CategorieController {
    private final CategorieService categorieService = new CategorieService();

    @FXML
    private Button buttonMaterielAdminC;

    @FXML
    private TextField newCategorieCodeField;
    @FXML
    private TableView<Categorie> categorieTableView;
    @FXML
    private TableColumn<Categorie, Integer> idColumn;
    @FXML
    private TableColumn<Categorie, String> libelleColumn;
    @FXML
    private TextField newCategorieLibelleField;
    @FXML
    private Button newCategorieButton;
    @FXML
    private VBox vbox1;
    @FXML
    private ToggleButton switchModeButton;

    @FXML
    private AnchorPane rootAnchorPane;

    private boolean isDarkMode = false;

    @FXML
    private void switchMode() {
        if (isDarkMode){
            rootAnchorPane.getStyleClass().remove("dark-mode");
            rootAnchorPane.getStyleClass().add("light-mode");
        } else {
            rootAnchorPane.getStyleClass().remove("light-mode");
            rootAnchorPane.getStyleClass().add("dark-mode");
        }
        isDarkMode = !isDarkMode;
    }


    public CategorieController() {
    }

    private void populateFields(Categorie categorie) {
        this.newCategorieLibelleField.setText(categorie.getLibelle());
    }

    @FXML
    void initialize() {
        this.categorieTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                this.populateFields(newSelection);
            }
        });
        //this.configureTableView();
        this.refreshTableView();
    }

    private void configureTableView() {
        this.idColumn.setCellValueFactory(new PropertyValueFactory("id"));
        this.libelleColumn.setCellValueFactory(new PropertyValueFactory("libelle"));
    }

    private void refreshTableView() {
        List<Categorie> categories = this.categorieService.getAll();
        ObservableList<Categorie> categorieObservableList = FXCollections.observableArrayList(categories);
        this.categorieTableView.setItems(categorieObservableList);
    }

    @FXML
    void handleNewCategorieButton(ActionEvent event) {
        String libelle = this.newCategorieLibelleField.getText().trim();
        if(libelle.isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Erreur : Veuillez entrer un libelle");
            alert.show();
            return;
        }
        if (!this.categorieService.rechercherCategorie(libelle)) {
            Categorie newCategorie = new Categorie(libelle,SessionManager.getCurrentUser().getId());
            this.categorieService.add(newCategorie);
            this.refreshTableView();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Ajout effectuee !");
            alert.showAndWait();
            this.newCategorieLibelleField.clear();
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("La categorie existe deja");
            alert.show();
            return;
        }
    }

    @FXML
    void handleDeleteCategorieButton(ActionEvent event) {
        Categorie selectedCategorie = (Categorie)this.categorieTableView.getSelectionModel().getSelectedItem();
        if (selectedCategorie != null) {
            try {
                this.categorieService.delete(selectedCategorie.getId());
                System.out.println("Suppression effectuée");
                this.refreshTableView();
            } catch (SQLException var3) {
                System.out.println("Erreur lors de la suppression :");
            }
        } else {
            System.out.println("Veuillez sélectionner une catégorie à supprimer.");
        }
    }

    @FXML
    void handleUpdateCategorieButton(ActionEvent event)
    {
        Categorie selectedCategorie = (Categorie)this.categorieTableView.getSelectionModel().getSelectedItem();
        String newLibelle = this.newCategorieLibelleField.getText().trim();

        if(newLibelle.isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Libelle est vide !!");
            alert.show();
            return;
        }

        if (selectedCategorie != null) {
            selectedCategorie.setLibelle(newLibelle);
            this.categorieService.update(selectedCategorie, selectedCategorie.getId());
            System.out.println("Modification effectuée");
            this.refreshTableView();
            this.newCategorieLibelleField.clear();
        } else {
            System.out.println("Veuillez sélectionner une catégorie et spécifier un nouveau libellé pour effectuer la modification.");
        }
    }

    @FXML
    void PageMaterielAdminC(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gestionressourcesmaterielles/MaterielInterfaceAdmin.fxml"));
            Parent root=loader.load();
            buttonMaterielAdminC.getScene().setRoot(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
