
package controllers;


import Entity.Demande;
import com.twilio.rest.api.v2010.account.call.Notification;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.util.*;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


import javafx.util.Duration;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.controlsfx.control.Notifications;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import service.DemandeService;

public class DemandeController implements Initializable {

    @FXML
    private Button clear;
    @FXML
    private Button add;

    @FXML
    private Button delete;

    @FXML
    private TableView<Demande> demandeTable;

    @FXML
    private TextArea description;
    @FXML
    private AnchorPane pricipal;

    @FXML
    private ChoiceBox<String> don;

    @FXML
    private TextField status;

    @FXML
    private TextField titre;

    @FXML
    private Button update;
    @FXML
    private TextField search;
    @FXML

    private TableColumn<Demande, Integer> id_demande;
    @FXML
    private TableColumn<Demande, String> descriptionD;
    @FXML
    private TableColumn<Demande, Integer> directeurCompagne;

    @FXML
    private TableColumn<Demande, Integer> donD;
    @FXML
    private TableColumn<Demande, Integer> RendezVous;

    @FXML
    private TableColumn<Demande, String> titreD;


    @FXML
    private TableColumn<Demande, Date> date;

    @FXML
    private TableColumn<Demande, String> statusD;
    private Map<Integer, String> typeDonMap = new HashMap<>();
    @FXML
    private TableColumn<Demande,Void> ActionC;

    private DemandeService demandeService;

    public DemandeController() {
        demandeService = new DemandeService();
    }
   /* @FXML
    private void initializeDonChoiceBox() {
        // Récupérer les types de don avec leurs IDs depuis la base de données
        typeDonMap = demandeService.getAllTypesWithIds();

        // Ajouter les types de don à la ChoiceBox
        ObservableList<String> donValues = FXCollections.observableArrayList(typeDonMap.values());
        don.setItems(donValues);

        // Ajouter un écouteur d'événements pour récupérer le type de don sélectionné
        don.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                int idDonSelectionne = getKeyFromValue(typeDonMap, newValue);
                System.out.println("ID du don sélectionné : " + idDonSelectionne);
                // Utilisez cet ID pour l'attribut de don dans votre demande
                // Par exemple, vous pouvez l'affecter à un attribut dans votre classe de demande
                // demande.setDonId(idDonSelectionne);
            }
        });
    }*/

    @FXML
    private int getKeyFromValue(Map<Integer, String> map, String value) {
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return -1; // Si la valeur n'est pas trouvée, retourne -1
    }
    private void initializeDemandeTable() {
        loadDemandes();
        // Associer les propriétés aux colonnes de la TableView
        id_demande.setCellValueFactory(new PropertyValueFactory<>("id_demande"));
        titreD.setCellValueFactory(new PropertyValueFactory<>("titre"));
        descriptionD.setCellValueFactory(new PropertyValueFactory<>("description"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusD.setCellValueFactory(new PropertyValueFactory<>("statut"));
        donD.setCellValueFactory(new PropertyValueFactory<>("don_id"));
        RendezVous.setCellValueFactory(new PropertyValueFactory<>("id_rendezvous"));
        directeurCompagne.setCellValueFactory(new PropertyValueFactory<>("directeurCampagne"));
        // Définir comment afficher la propriété RendezVous dans la cellule de la colonne
       /* RendezVous.setCellFactory(column -> {
            return new TableCell<Demande, Integer>() {
                @Override
                protected void updateItem(Integer idRendezvous, boolean empty) {
                    super.updateItem(idRendezvous, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        if (idRendezvous == 0) {
                            setText("Pas de rendez-vous");
                        } else {
                            setText("Rendez-vous ID: " + idRendezvous);
                        }
                    }
                }
            };
        });*/


        // Ajouter un écouteur d'événements pour mettre à jour le texte de la ChoiceBox lorsque la sélection de la TableView change
        demandeTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                int idDonSelectionne = newValue.getDon_id(); // Récupérer l'ID du don à partir de la demande sélectionnée
                String typeDon = getTypeDonById(idDonSelectionne); // Récupérer le type de don correspondant à partir de l'ID
                if (typeDon != null) {
                    don.setValue(typeDon); // Définir le texte de la ChoiceBox sur le type de don
                }
            }
        });

    }
    private void loadDemandes() {
        List<Demande> demandes = demandeService.getAlltrie();
        ObservableList<Demande> observableDemandes = FXCollections.observableArrayList(demandes);
        demandeTable.setItems(observableDemandes);

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeOtherComponents();
     /*   try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/exemple/pidevd/sample.fxml"));
            Node sidebarContent = loader.load();

            // Récupérer le contrôleur de sample.fxml si nécessaire
            sample sidebarController = loader.getController();

            // Ajouter le contenu de sample.fxml au sidebarContainer
            pricipal.getChildren().add(sidebarContent);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
private void initializeOtherComponents() {
    initializeDemandeTable();
  //  statt();
    // demandeTable.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
    // Style pour l'entête de la TableView
    //  demandeTable.setStyle("-fx-control-inner-background: #2196F3;");

// Style pour le contenu de la TableView
           /* id_demande.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #000000; -fx-padding: 5px;");
            descriptionD.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #000000; -fx-padding: 5px;");
            donD.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #000000; -fx-padding: 5px;");
            titreD.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #000000; -fx-padding: 5px;");
            date.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #000000; -fx-padding: 5px;");
            statusD.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #000000; -fx-padding: 5px;");
            ActionC.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #000000; -fx-padding: 5px;");
            RendezVous.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #000000; -fx-padding: 5px;");
            directeurCompagne.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #000000; -fx-padding: 5px;");
           */
    search();
    //  initializeDonChoiceBox();
    // Ajouter un écouteur de sélection à la TableView
    demandeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
        if (newSelection != null) {
            // Déplacer les données de la demande sélectionnée vers les champs de texte
            titre.setText(newSelection.getTitre());
            description.setText(newSelection.getDescription());
            status.setText(newSelection.getStatut());
            // don.setValue(newSelection.getDon_id());

        } else {
            // Effacer les champs de texte si aucune demande n'est sélectionnée
            titre.clear();
            description.clear();
            status.clear();
            don.getSelectionModel().clearSelection();
        }
    });
    ActionC.setCellFactory(param -> new TableCell<>() {
        private final Button acceptButton = new Button();
        private final Button rejectButton = new Button();
        {
            acceptButton.setOnAction(event -> {
                int index = getIndex();
                if (index >= 0 && index < getTableView().getItems().size()) {
                    accepterDemande(index);
                }
            });
            Glyph acceptIcon = new Glyph("FontAwesome", FontAwesome.Glyph.CHECK);
            acceptIcon.setColor(Color.rgb(0, 128, 255)); // Définir une couleur bleue médicale
            acceptButton.setGraphic(acceptIcon);

            Glyph rejectIcon = new Glyph("FontAwesome", FontAwesome.Glyph.TIMES);
            rejectIcon.setColor(Color.RED); // Définir la couleur rouge pour l'icône
            rejectButton.setGraphic(rejectIcon);
            acceptButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
            rejectButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");

            rejectButton.setOnAction(event -> {
                int index = getIndex();
                if (index >= 0 && index < getTableView().getItems().size()) {
                    rejeterDemande(index);
                }
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                HBox buttons = new HBox(5, acceptButton, rejectButton);
                setGraphic(buttons);
            }
        }
    });



}


   /* @FXML
   private void initialize() {
        loadDemandes();
        initializeDemandeTable();
        statt();
        demandeTable.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        // Style pour l'entête de la TableView
        demandeTable.setStyle("-fx-control-inner-background: #2196F3;");

// Style pour le contenu de la TableView
        id_demande.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #000000; -fx-padding: 5px;");
        descriptionD.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #000000; -fx-padding: 5px;");
        donD.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #000000; -fx-padding: 5px;");
        titreD.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #000000; -fx-padding: 5px;");
        date.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #000000; -fx-padding: 5px;");
        statusD.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #000000; -fx-padding: 5px;");
        ActionC.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #000000; -fx-padding: 5px;");
        RendezVous.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #000000; -fx-padding: 5px;");


        search();
     //  initializeDonChoiceBox();
       // Ajouter un écouteur de sélection à la TableView
       demandeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
           if (newSelection != null) {
               // Déplacer les données de la demande sélectionnée vers les champs de texte
               titre.setText(newSelection.getTitre());
               description.setText(newSelection.getDescription());
               status.setText(newSelection.getStatut());
              // don.setValue(newSelection.getDon_id());

           } else {
               // Effacer les champs de texte si aucune demande n'est sélectionnée
               titre.clear();
               description.clear();
               status.clear();
               don.getSelectionModel().clearSelection();
           }
       });
        ActionC.setCellFactory(param -> new TableCell<>() {
            private final Button acceptButton = new Button("Accepter");
            private final Button rejectButton = new Button("Rejeter");

            {
                acceptButton.setOnAction(event -> {
                    int index = getIndex();
                    if (index >= 0 && index < getTableView().getItems().size()) {
                        accepterDemande(index);
                    }
                });

                rejectButton.setOnAction(event -> {
                    int index = getIndex();
                    if (index >= 0 && index < getTableView().getItems().size()) {
                        rejeterDemande(index);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, acceptButton, rejectButton);
                    setGraphic(buttons);
                }
            }
        });



    }*/
    private void accepterDemande(int index) {
        Demande demande = demandeTable.getItems().get(index);
        int demandeId = demande.getId_demande();
        passerARendezVous(demandeId);
    }
    private sample sampleController;

    public void initSampleController(sample controller) {
        sampleController = controller;
    }
    private void passerARendezVous(int demandeId) {
       /* try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/exemple/pidevd/RendezVous.fxml"));
            Parent root = loader.load();
            clear.getScene().setRoot(root);
            RendezVousController rendezVousController = loader.getController();
            rendezVousController.initData(demandeId); // Passer l'ID de la demande au contrôleur

        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
       // sample sampleController = getSampleController(); // Remplacez getSampleController() par votre propre méthode pour obtenir la référence au SampleController
        sampleController.afficherRendezVous(demandeId);
    }



    private void rejeterDemande(int index) {
        Demande demande = demandeTable.getItems().get(index);
        int demandeId = demande.getId_demande();
        if (demande.getStatut().equals("REFUSEE")) {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Demande déjà refusée", "Cette demande a déjà été refusée.");
        } else if (demande.getStatut().equals("DEMANDE TRAITEE")) {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Demande déjà acceptée", "Cette demande a déjà été acceptée.");
        } else {
            // Appelle la méthode Rejetee du service pour rejeter la demande dans la base de données
            demandeService.Rejetee(demandeId);
             loadDemandes();
            // Désactive le bouton "Accepter" pour cette demande
            for (Node node : demandeTable.lookupAll(".table-cell")) {
                if (node instanceof TableCell && ((TableCell<?, ?>) node).getIndex() == index) {
                    TableCell<Demande, ?> cell = (TableCell<Demande, ?>) node;
                    for (Node buttonNode : cell.getChildrenUnmodifiable()) {
                        if (buttonNode instanceof Button && ((Button) buttonNode).getText().equals("Accepter")) {
                            ((Button) buttonNode).setDisable(true);
                            break;
                        }
                    }
                    break;
                }
            }

            // Affiche un avertissement pour informer l'utilisateur que la demande a été refusée
            showAlert(Alert.AlertType.WARNING, "Demande rejetée", "Demande rejetée", "La demande a été refusée.");
        }
    }



    @FXML
    void search() {
        id_demande.setCellValueFactory(new PropertyValueFactory<>("id_demande"));
        titreD.setCellValueFactory(new PropertyValueFactory<>("titre"));
        descriptionD.setCellValueFactory(new PropertyValueFactory<>("description"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        statusD.setCellValueFactory(new PropertyValueFactory<>("statut"));
        donD.setCellValueFactory(new PropertyValueFactory<>("don_id"));

        List<Demande> demandes = demandeService.getAlltrie();
        ObservableList<Demande> observableDemandes = FXCollections.observableArrayList(demandes);
        demandeTable.setItems(observableDemandes);

        FilteredList<Demande> filtereddata = new FilteredList<>(observableDemandes, b -> true);
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            filtereddata.setPredicate(demande -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                // Filtrer uniquement sur l'ID
                return String.valueOf(demande.getId_demande()).toLowerCase().contains(lowerCaseFilter);
            });
        });
        // Appliquer le filtre à la table
        SortedList<Demande> sortedData = new SortedList<>(filtereddata);
        sortedData.comparatorProperty().bind(demandeTable.comparatorProperty());
        demandeTable.setItems(sortedData);
    }

    private String getTypeDonById(int idDon) {
        for (Map.Entry<Integer, String> entry : typeDonMap.entrySet()) {
            if (entry.getKey() == idDon) {
                return entry.getValue();
            }
        }
        return null; // Retourne null si aucun type de don correspondant n'est trouvé
    }





    @FXML
    protected void handleAddDemande() {
        String donType = don.getValue(); // Récupère le type de don depuis le choix de l'utilisateur
        int donId = getKeyFromValue(typeDonMap, donType); // Récupère l'ID du don correspondant au type sélectionné

        if (donId != -1) { // Vérifie si l'ID du don est valide
            String descriptionText = description.getText(); // Récupère la description depuis la zone de texte
            String titreText = titre.getText();
            Demande nouvelleDemande = new Demande(donId, new Date(), descriptionText, titreText, 1);
            // Ajoutez la nouvelle demande via le service
            demandeService.add(nouvelleDemande);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Demande Ajout", "La demande a été ajoutée avec succès.");

            // Actualisez le tableau des demandes
            initializeDemandeTable();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Ajout de demande", "Veuillez sélectionner un type de don valide.");
        }
    }
    /*void statt(){
        List<Demande> demandes = demandeService.getAll();

        // Comptage des demandes par directeur de campagne avec rendez-vous
        Map<Integer, Integer> statistiques = new HashMap<>();
        for (Demande demande : demandes) {
            int directeurId = demande.getDirecteurCampagne();
            int rendezvousId = demande.getId_rendezvous();

            // Vérifier si la demande a un rendez-vous associé
            if (rendezvousId != 0) {
                statistiques.put(directeurId, statistiques.getOrDefault(directeurId, 0) + 1);
            }
        }

        // Convertir les statistiques en une liste d'entrées
        List<Map.Entry<Integer, Integer>> statistiquesList = new ArrayList<>(statistiques.entrySet());

        // Créer une liste d'objets PieChart.Data pour le diagramme circulaire
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<Integer, Integer> entry : statistiquesList) {
            int directeurId = entry.getKey();
            int nombreDemandes = entry.getValue();
            pieChartData.add(new PieChart.Data("Directeur " + directeurId, nombreDemandes));
        }

        // Créer le PieChart
        pieCHART.setData(pieChartData);
        pieCHART.setTitle("Statistiques par Directeur de Campagne");

    }*/
    @FXML
    protected void Stat() {
        /*List<Demande> demandes = demandeService.getAll();
        // Comptage des demandes par directeur de campagne
       Map<Integer, Integer> statistiques = new HashMap<>();
        for (Demande demande : demandes) {
            int directeurId = demande.getDirecteurCampagne();
            statistiques.put(directeurId, statistiques.getOrDefault(directeurId, 0) + 1);
        }

        // Affichage des statistiques
        for (Map.Entry<Integer, Integer> entry : statistiques.entrySet()) {
            int directeurId = entry.getKey();
            int nombreDemandes = entry.getValue();
            System.out.println("Directeur de campagne " + directeurId + " : " + nombreDemandes + " demandes");
        }*/
        List<Demande> demandes = demandeService.getAll();

// Comptage des demandes par directeur de campagne avec rendez-vous
        Map<Integer, Integer> statistiques = new HashMap<>();
        for (Demande demande : demandes) {
            int directeurId = demande.getDirecteurCampagne();
            int rendezvousId = demande.getId_rendezvous();

            // Vérifier si la demande a un rendez-vous associé
            if (rendezvousId != 0) { // Modifier cette condition en fonction de votre logique réelle pour identifier les demandes avec rendez-vous
                statistiques.put(directeurId, statistiques.getOrDefault(directeurId, 0) + 1);
            }
        }

// Convertir la Map en une liste d'entrées triées
        List<Map.Entry<Integer, Integer>> statistiquesList = new ArrayList<>(statistiques.entrySet());
        statistiquesList.sort(Map.Entry.comparingByKey(Collections.reverseOrder()));

// Construction de la chaîne de statistiques
        StringBuilder statistiquesString = new StringBuilder();
        for (Map.Entry<Integer, Integer> entry : statistiquesList) {
            int directeurId = entry.getKey();
            int nombreDemandes = entry.getValue();
            statistiquesString.append("Directeur de campagne ").append(directeurId).append(" : ").append(nombreDemandes).append(" demandes\n");
        }

// Afficher les statistiques
        System.out.println(statistiquesString.toString()); }

// Retourner les statistiques sous forme de chaîne de caractères



    @FXML
    protected void handleDeleteDemande() {
        // Récupérer la demande sélectionnée dans la TableView
        Demande demandeSelectionnee = demandeTable.getSelectionModel().getSelectedItem();

        if (demandeSelectionnee != null) {
            // Demander confirmation à l'utilisateur avant de supprimer la demande
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation");
            confirmation.setHeaderText("Supprimer la demande");
            confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette demande ?");

            // Attendre la réponse de l'utilisateur
            Optional<ButtonType> result = confirmation.showAndWait();

            // Si l'utilisateur a confirmé la suppression
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Supprimer la demande à partir du service
                demandeService.delete(demandeSelectionnee.getId_demande());

                // Actualiser la TableView
                loadDemandes();
            }
        } else {
            // Aucune demande sélectionnée, afficher un message d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner une demande à supprimer.");
            alert.showAndWait();
        }
    }
    @FXML
    private void handleUpdateDemande() {
        Demande demandeSelectionnee = demandeTable.getSelectionModel().getSelectedItem();
        if (demandeSelectionnee != null) {
            // Récupérer les nouvelles valeurs des champs
            String newTitre = titre.getText();
            String newDescription = description.getText();
            String newStatus = status.getText();
            String donType = don.getValue(); // Récupère le type de don depuis le choix de l'utilisateur
            int donId = getKeyFromValue(typeDonMap, donType);
            Demande demande = new Demande(donId, new Date(), newDescription, newTitre, 1);
            demandeService.update(demande,demandeSelectionnee.getId_demande());
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Demande mise à jour", "La demande a été mise à jour avec succès.");
            initializeDemandeTable();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucune demande sélectionnée", "Veuillez sélectionner une demande à mettre à jour.");
        }
    }
    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }


}
