package controllers;


import Entity.Demande;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import Entity.User; // Import the User entity if not already imported
import controllers.SessionManager; // Import the SessionManager service if not already imported
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.controlsfx.control.Notifications;
import service.DemandeService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class sample implements Initializable {

    @FXML
    private Label Menu;
    @FXML
    private Label userNameLabel;
    @FXML
    private Button actualiteback;

    @FXML
    private Label MenuBack;

    @FXML
    private AnchorPane slider;

    @FXML
    private AnchorPane contenu;
    @FXML
    private Button Calendrier;
    public AnchorPane getContenu() {
        return contenu;
    }
    private boolean isInitialized = false;
    @FXML
    private Button demande;
    @FXML
    private Button categorie;
    @FXML
    private Button materiel;

    @FXML
    private Button rendezvous;

    private DemandeController demandeController;
    private DemandeService demandeService;

    public sample() {
        demandeService = new DemandeService();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminPage.fxml"));
            Parent root = loader.load();

            AdminPage demandeController = loader.getController();
            demandeController.initSampleController(this); // Passez la référence du SampleController au DemandeController

            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Retrieve the current user from the session
        User currentUser = SessionManager.getCurrentUser();

        // Display the name of the current user
        if (currentUser != null) {
            userNameLabel.setText(currentUser.getNom() + " " + currentUser.getPrenom());
        }
       /* try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/exemple/pidevd/Demande.fxml"));
            Node sidebarContent = loader.load();

            // Récupérer le contrôleur de Demande.fxml si nécessaire
            DemandeController demandeController = loader.getController();

            // Ajouter le contenu initial de Demande.fxml à l'AnchorPane contenu
            contenu.getChildren().setAll(sidebarContent);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        slider.setTranslateX(0);


        Menu.setOnMouseClicked(event ->{
            TranslateTransition slide=new TranslateTransition();
            slide.setDuration(Duration.seconds(0.5));
            slide.setNode(slider);
            slide.setToX(0);
            slide.play();
            slider.setTranslateX(-176);
            slide.setOnFinished((ActionEvent e)->{
              Menu.setVisible(false);
              MenuBack.setVisible(true);
            });
        } );
        MenuBack.setOnMouseClicked(event ->{
            TranslateTransition slide=new TranslateTransition();
            slide.setDuration(Duration.seconds(0.5));
            slide.setNode(slider);
            slide.setToX(-176);
            slide.play();
            slider.setTranslateX(0);
            slide.setOnFinished((ActionEvent e)->{
                Menu.setVisible(true);
                MenuBack.setVisible(false);
            });
        } );


    }
    @FXML
    protected void afficherDemande() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Demande.fxml"));
            Parent root = loader.load();

            DemandeController demandeController = loader.getController();
            demandeController.initSampleController(this); // Passez la référence du SampleController au DemandeController

            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void afficherRendezVous(int idDemande) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RendezVous.fxml"));
            Parent root = loader.load();

            RendezVousController rendezVousController = loader.getController();
            // Configurez le contrôleur de la vue RendezVous si nécessaire
            rendezVousController.initData(idDemande);
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void afficherCalendrier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/calendrier.fxml"));
            Parent root = loader.load();

            CalenderController CalenController = loader.getController();
            // Configurez le contrôleur de la vue RendezVous si nécessaire
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void afficherStatistique() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Statistique.fxml"));
            Parent root = loader.load();

            StatControllerT SController = loader.getController();
            // Configurez le contrôleur de la vue RendezVous si nécessaire
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void afficherUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminPage.fxml"));
            Parent root = loader.load();

            AdminPage AdminPage = loader.getController();
            AdminPage.initSampleController(this);
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void afficherStatUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/StatController.fxml"));
            Parent root = loader.load();

            StatController StatController = loader.getController();
            // Configurez le contrôleur de la vue RendezVous si nécessaire
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void afficherStatEVENTT() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardEvent.fxml"));
            Parent root = loader.load();

            DashboardController dashboardController = loader.getController();
            // Configurez le contrôleur de la vue RendezVous si nécessaire
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void afficherStatCOM() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Statistics.fxml"));
            Parent root = loader.load();

            StatisticsController StatisticsController = loader.getController();
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void afficheractualiteBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Actualites.fxml"));
            Parent root = loader.load();

            ActualiteController actualiteController = loader.getController();
            // Configurez le contrôleur de la vue RendezVous si nécessaire
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void afficherEvenementBack(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Evenements.fxml"));
            Parent root = loader.load();

            EvenementController evenementController = loader.getController();
           evenementController.initSampleController(this);
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void afficherStatEvent() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardEvent.fxml"));
            Parent root = loader.load();

            DashboardController DashboardController = loader.getController();

            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    @FXML
    protected void afficherMateriels() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MaterielInterfaceAdmin.fxml"));
            Parent root = loader.load();

            MaterielController MaterielController = loader.getController();
            // Configurez le contrôleur de la vue RendezVous si nécessaire
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    protected void afficherChambreAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChambreInterfaceAdmin.fxml"));
            Parent root = loader.load();

            ChambreController  chambreController = loader.getController();
            // Configurez le contrôleur de la vue RendezVous si nécessaire
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    protected void afficherPersonnelAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PersonnelInterfaceAdmin.fxml"));
            Parent root = loader.load();

            PersonnelController  personnelController = loader.getController();
            // Configurez le contrôleur de la vue RendezVous si nécessaire
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    protected void afficherCategorie() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CategorieInterfaceAdmin.fxml"));
            Parent root = loader.load();
            CategorieController CategorieController = loader.getController();
            // Configurez le contrôleur de la vue RendezVous si nécessaire
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void afficherRendezVous() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RendezVous.fxml"));
            Parent root = loader.load();
            RendezVousController RendezVousController = loader.getController();
            // Configurez le contrôleur de la vue RendezVous si nécessaire
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void afficherCompagne() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Campagne.fxml"));
            Parent root = loader.load();
            CampagneController CampagneController = loader.getController();
            CampagneController.initSampleController(this);
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void afficherDon() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Don.fxml"));
            Parent root = loader.load();
            DonController DonController = loader.getController();
            // Configurez le contrôleur de la vue RendezVous si nécessaire
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void logout(ActionEvent event) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Logout");
        alert.setHeaderText("Logout");
        alert.setContentText("Are you sure you want to log out?");

        // Handle user's choice
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // User confirmed logout
                SessionManager.clearSession(); // Clear the session
                // Close the current window (assuming this is the only stage)
                Stage stage = (Stage) contenu.getScene().getWindow();
                stage.close();
                // You might want to open a login window here
                // Example:
                LoginController.showLoginWindow();
            }
        });
    }
    @FXML
    void EditProfile(ActionEvent event) {
        try {

            Parent root = FXMLLoader.load(getClass().getResource("/EditProfile.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void ExportExcel() {

        List<Demande> demandes = demandeService.getAll();
        String filePath = "export.xlsx";
        exportToExcel(demandes, filePath);
        Notifications.create()
                .title("Notification")
                .text("Données exportées vers Excel. !")
                .position(Pos.TOP_RIGHT)
                .hideAfter(Duration.seconds(5))
                .show();
        System.out.println("Données exportées vers Excel.");

    }

    public void exportToExcel(List<Demande> demandes, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Demandes");

            // Style pour l'état "En cours de traitement" (noir)
            CellStyle enCoursStyle = workbook.createCellStyle();
            enCoursStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            enCoursStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Style pour l'état "Refusé" (rouge)
            CellStyle refuseStyle = workbook.createCellStyle();
            refuseStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
            refuseStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Style pour l'état "Demande traitée" (vert)
            CellStyle traiteStyle = workbook.createCellStyle();
            traiteStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            traiteStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            // Créer une ligne d'en-tête avec les attributs
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Titre");
            headerRow.createCell(1).setCellValue("Description");
            headerRow.createCell(2).setCellValue("Date");
            headerRow.createCell(3).setCellValue("ID du directeur de campagne");
            headerRow.createCell(4).setCellValue("État");
            headerRow.createCell(5).setCellValue("ID du rendez-vous");

            // Parcourir les demandes et les écrire dans le fichier Excel
            int rowNum = 1;
            for (Demande demande : demandes) {
                Row row = sheet.createRow(rowNum++);
                int colNum = 0;
                row.createCell(colNum++).setCellValue(demande.getTitre());
                row.createCell(colNum++).setCellValue(demande.getDescription());
                row.createCell(colNum++).setCellValue(demande.getDate());
                row.createCell(colNum++).setCellValue(demandeService.getEmailDirecteur(demande.getDirecteurCampagne()));
                Cell cell = row.createCell(colNum);
                cell.setCellValue(demande.getStatut());

                // Appliquer la mise en forme conditionnelle en fonction de l'état de la demande
                String etat = demande.getStatut();
                if (etat.equals("ENCOURS DE TRAITMENT")) {
                    cell.setCellStyle(enCoursStyle);
                } else if (etat.equals("REFUSEE")) {
                    cell.setCellStyle(refuseStyle);
                } else if (etat.equals("DEMANDE TRAITEE")) {
                    cell.setCellStyle(traiteStyle);
                    row.createCell(5).setCellValue(demande.getId_rendezvous());
                }
            }
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
