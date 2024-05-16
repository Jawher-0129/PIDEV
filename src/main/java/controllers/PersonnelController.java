package controllers;

import Entity.Materiel;
import Entity.Personnel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.scene.image.ImageView;
import java.io.File;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.ChoiceBox;


import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import service.ExcelExporter;
import service.PersonnelService;

import java.io.IOException;
public class PersonnelController {
    private final PersonnelService personnelService = new PersonnelService();
    @FXML
    private VBox personnelCardsContainer;
    @FXML
    private TextField RatingPersonnel;

    @FXML
    private TextField experiencePersonnel;
    @FXML
    private Label Disponibilite;

    @FXML
    private CheckBox DisponibleRadioButton;

    @FXML
    private TextField Nom;

    @FXML
    private TextField Prenom_personnel;

    @FXML
    private Button choisir;

    @FXML
    private Button ModifierPersonnelBtn;

    @FXML
    private CheckBox nonDisponibleRadioButton;

    @FXML
    private ChoiceBox<String> roleChoiceBox;
    @FXML
    private ImageView imageView;
    @FXML
    private TableColumn<Personnel, Integer> experience;

    @FXML
    private TableColumn<Personnel, Integer> idpersonnel;
    @FXML
    private TableColumn<Personnel, String> nompersonnel;
    @FXML
    private TableView<Personnel> tablePersonnel;
    @FXML
    private TableColumn<Personnel, String> prenompersonnel;
    @FXML
    private TableColumn<Personnel, Integer> disponibilite;

    @FXML
    private TableColumn<Personnel, String> image;
    @FXML
    private TableColumn<Personnel, Integer> rating;
    @FXML
    private TableColumn<Personnel, String> role;

    @FXML
    private TableColumn<Personnel, Integer> userid;

    @FXML
    private Button SupprimerPersonnelBtn;

    @FXML
    private Button AjouterPersonnelBtn;

    @FXML
    private Button downloadPdfButton;

    @FXML
    private Button showStaticsButton;
    @FXML
    private Button SMSButton;
    @FXML
    private Button EmailButton;
    private Image loadImage(String imageUrl) {
        // Implement your logic to load image from URL or file
        return new Image(imageUrl);
    }




    @FXML
    private void initializePersonnelChoiceBox() {
        roleChoiceBox.setItems(FXCollections.observableArrayList(
                "Chirurgie", "Neurologie", "Infirmier", "Cardiologie", "médecin Généraliste", "Médecin urgentiste", "ophtalmologue"
        ));
        roleChoiceBox.setValue("Chirurgie"); // Définir la valeur initiale

    }

    private void populateFields(Personnel personnel) {

        this.Nom.setText(personnel.getNom());
        this.Prenom_personnel.setText(personnel.getPrenom_personnel());
        this.experiencePersonnel.setText(Integer.toString(personnel.getExperience()));
        this.RatingPersonnel.setText(Integer.toString(personnel.getRating()));
        this.roleChoiceBox.setValue(personnel.getRole());

        //this.DisponibleRadioButton.setSelected(personnel.getDisponibilite() == 1);
        if (personnel.getDisponibilite() == 1) {
            this.DisponibleRadioButton.setSelected(personnel.getDisponibilite() == 1);
        } else
            this.nonDisponibleRadioButton.setSelected(personnel.getDisponibilite() == 0);



        // Populate the roleChoiceBox with options
        ObservableList<String> personnels = FXCollections.observableArrayList("Radiologie", "Chirurgie", "Infirmier", "Neurologie");
        roleChoiceBox.setItems(personnels);

        // Set up a listener for the roleChoiceBox selection
        roleChoiceBox.setOnAction(event -> {
            String selectedRole = roleChoiceBox.getValue(); // Get the selected role
            if (selectedRole != null && selectedRole.equals(personnel.getRole())) {
                roleChoiceBox.setValue(personnel.getRole());
            }
        });


    }

    @FXML
    void initialize() {
        configureTableView();
        initializePersonnelChoiceBox();
        // Ajouter un écouteur de sélection à la TableView
        tablePersonnel.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Déplacer les données du personnel sélectionné vers les champs de texte
                Nom.setText(newSelection.getNom());
                Prenom_personnel.setText(newSelection.getPrenom_personnel());
                roleChoiceBox.setValue(newSelection.getRole());
                experiencePersonnel.setText(String.valueOf(newSelection.getExperience()));
                RatingPersonnel.setText(String.valueOf(newSelection.getRating()));

                // Affichage du QR Code
                showPersonnelQRCode(newSelection);
            } else {
                // Effacer les champs de texte si aucun personnel n'est sélectionné
                clearPersonnelDetails();
            }
        });
    }

    private void showPersonnelQRCode(Personnel personnel) {
        if (personnel != null) {
            String qrText = "Nom: " + personnel.getNom() +
                    ", Prénom: " + personnel.getPrenom_personnel() +
                    ", Rôle: " + personnel.getRole() +
                    ", Disponibilité: " + (personnel.getDisponibilite() == 1 ? "Disponible" : "Non disponible");
            Image qrImage = personnelService.generateQRCodeImage(qrText);
            imageView.setImage(qrImage);  // Assurez-vous que imageView est l'ImageView où vous voulez afficher le QR Code
        }
    }

    private void clearPersonnelDetails() {
        Nom.clear();
        Prenom_personnel.clear();
        experiencePersonnel.clear();
        RatingPersonnel.clear();
        roleChoiceBox.getSelectionModel().clearSelection();
        imageView.setImage(null);  // Effacer l'image précédente du QR Code
    }

    private void configureTableView() {
        System.out.println("Configuration de la TableView...");
        idpersonnel.setCellValueFactory(new PropertyValueFactory<>("id_personnel"));
        nompersonnel.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenompersonnel.setCellValueFactory(new PropertyValueFactory<>("prenom_personnel"));
        experience.setCellValueFactory(new PropertyValueFactory<>("experience"));
        role.setCellValueFactory(new PropertyValueFactory<>("role"));
        rating.setCellValueFactory(new PropertyValueFactory<>("rating"));
        userid.setCellValueFactory(new PropertyValueFactory<>("user_id_id"));
        disponibilite.setCellValueFactory(new PropertyValueFactory<>("disponibilite"));
        image.setCellValueFactory(new PropertyValueFactory<>("image"));
        loadPersonnels();
    }

    private void loadPersonnels() {
        System.out.println("Chargement des personnels depuis la base de données...");
        List<Personnel> Personnels = personnelService.getAll();
        System.out.println("Nombre de personnels récupérés : " + Personnels.size());
        ObservableList<Personnel> observablePersonnels = FXCollections.observableArrayList(Personnels);
        System.out.println("Personnels chargés avec succès.");
        tablePersonnel.setItems(observablePersonnels);
    }


    @FXML
    void handleAjouterPersonnel(ActionEvent event) {

        String Nom = this.Nom.getText();
        String prenom = this.Prenom_personnel.getText();
        String role = this.roleChoiceBox.getValue();
        String experiencetext = experiencePersonnel.getText();
        String ratingtext = RatingPersonnel.getText();

        int experience = Integer.parseInt(experiencetext);
        int rating = Integer.parseInt(ratingtext);
        int disponibilite;

        if (nonDisponibleRadioButton.isSelected()) {
            disponibilite = 0;
        } else {
            disponibilite = 1;
        }
        String imageURL = imageView.getImage() != null ? imageView.getImage().getUrl() : null;
        String imageName = imageURL != null ? new File(imageURL).getName() : null;
        String image = "C:\\\\Users\\\\jawhe\\\\OneDrive\\\\Bureau\\\\3A32HealthSwiftIntegration (2)\\\\3A32HealthSwiftIntegration\\\\3A32HealthSwift\\\\public\\\\uploads\\\\" + imageName;


        if (image == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Erreur : Veuillez choisir une image");
            alert.show();
            return;
        }


        if (Nom.length() == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Erreur : Veuillez entrer un Nom");
            alert.show();
            return;
        }


        String Role = roleChoiceBox.getValue();

        if (Role.length() == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Erreur : Veuillez entrer un role.");
            alert.show();
            return;
        }


        if (experience < 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Erreur : Veuillez entrer une experience positif");
            alert.show();
            return;
        }


        if (image == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Erreur : Veuillez choisir une image");
            alert.show();
            return;
        }
        System.out.println(image);

        Personnel newPer = new Personnel(Nom, prenom, disponibilite, role, experience, image, rating, SessionManager.getCurrentUser().getId());
        this.personnelService.add(newPer);
        loadPersonnels();
    }
    @FXML
    void handleChoisirImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            imageView.setImage(image);
        }
    }

    @FXML
    void excelPersonnel(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                ExcelExporter.exportToExcel(tablePersonnel, "Personnels Data", file.getAbsolutePath());
                System.out.println("Excel téléchargée avec succèes");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur");
        }

    }

    @FXML
    void handleModifierPersonnel(ActionEvent event) {
        Personnel personnelSelectionne = tablePersonnel.getSelectionModel().getSelectedItem();
        if (personnelSelectionne != null) {
            String Nom = this.Nom.getText();
            String prenom = this.Prenom_personnel.getText();
            String role = this.roleChoiceBox.getValue();
            String experiencetext = experiencePersonnel.getText();
            String ratingtext = RatingPersonnel.getText();

            int experience = Integer.parseInt(experiencetext);
            int rating = Integer.parseInt(ratingtext);
            int disponibilite;

            if (nonDisponibleRadioButton.isSelected()) {
                disponibilite = 0;
            } else {
                disponibilite = 1;
            }
            String imageURL = imageView.getImage() != null ? imageView.getImage().getUrl() : null;
            String imageName = imageURL != null ? new File(imageURL).getName() : null;
            String image = "C:\\\\Users\\\\jawhe\\\\OneDrive\\\\Bureau\\\\3A32HealthSwiftIntegration (2)\\\\3A32HealthSwiftIntegration\\\\3A32HealthSwift\\\\public\\\\uploads\\\\" + imageName;

            if (image == null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("Erreur : Veuillez choisir une image");
                alert.show();
                return;
            }
            Personnel newPer = new Personnel(Nom, prenom, disponibilite, role, experience, image, rating, 8);
            this.personnelService.update(newPer, personnelSelectionne.getId_personnel());
            loadPersonnels();

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Personnel mis à jour", "Le personnel a été mis à jour avec succès.");

        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun personnel sélectionnée", "Veuillez sélectionner une personnel à mettre à jour.");
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
    void handleSupprimerPersonnel(ActionEvent event) {
        Personnel selectedPersonnel = this.tablePersonnel.getSelectionModel().getSelectedItem();
        if (selectedPersonnel != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Êtes-vous sûr de vouloir supprimer ce personnel ?");
            alert.setContentText("Cette action est irréversible.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    personnelService.delete(selectedPersonnel.getId_personnel());
                    System.out.println("Suppression effectuée");
                    loadPersonnels();
                } catch (RuntimeException e) {
                    System.out.println("Erreur lors de la suppression : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun personnel sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un personnel à supprimer.");
            alert.showAndWait();
        }
    }


    @FXML
    void handleDownloadPdfButtonAction(ActionEvent event) {
        try {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 700); // Starting position
            PDType1Font font = PDType1Font.HELVETICA;

            String text = "List of personals.";
            String[] lines = text.split("\n");

            for (String line : lines) {
                contentStream.newLineAtOffset(15, -15);
                contentStream.setFont(font, 12);
                contentStream.showText(line);
                contentStream.newLine();
            }

            ObservableList<Personnel> personnellist = tablePersonnel.getItems();
            float startY = 650; // Initial vertical position
            float lineHeight = 15; // Height of each line

            for (Personnel personnel : personnellist) {
                contentStream.newLineAtOffset(0, -20); // Add some space between each user

                contentStream.showText("Nom: " +personnel.getNom());
                contentStream.newLineAtOffset(0, -lineHeight); // Move to the next line
                contentStream.showText("Prenom: " + personnel.getPrenom_personnel());
                contentStream.newLineAtOffset(0, -lineHeight);
                contentStream.showText("Disponibilite: " + personnel.getDisponibilite());
                contentStream.newLineAtOffset(0, -lineHeight);
                contentStream.showText("Role: " + personnel.getRole());
                contentStream.newLineAtOffset(0, -lineHeight);
                contentStream.showText("Experience: " + personnel.getExperience());
                contentStream.newLineAtOffset(0, -lineHeight);
                contentStream.showText("Rating: " + personnel.getRating());
                contentStream.newLineAtOffset(0, -lineHeight);
                contentStream.newLineAtOffset(0, -20); // Add some space between each user
            }

            contentStream.endText();
            contentStream.close();

            document.save("Personnels.pdf");
            document.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("PDF Généré");
            alert.setHeaderText(null);
            alert.setContentText("Le document PDF contenant la liste des materielles a été généré avec succès !");
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de la génération du PDF");
            alert.setContentText("Une erreur est survenue lors de la génération du PDF. Veuillez réessayer.");
            alert.showAndWait();
        }

    }
    @FXML
    void handlePersonnelStatics (ActionEvent event){
        try {
            // Charger la fenêtre des statistiques du personnel à partir du fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PersonnelStatics.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Créer une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Statistiques du Personnel");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void sendEmail(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Email.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Créer une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(" Email pour notre top Personnels");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @FXML
    void envoyerSMS(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SMSPersonnel.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Créer une nouvelle fenêtre
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("SMS pour notre top  Personnels");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}

