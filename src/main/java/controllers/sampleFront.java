package controllers;

import Entity.*;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import service.DemandeService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import Entity.User; // Import the User entity if not already imported

public class sampleFront implements Initializable {
    @FXML
    private Button categorie;
    @FXML
    private Label userNameLabel;
    @FXML
    private AnchorPane contenu;
    @FXML
    private Label Menu;

    @FXML
    private Label MenuBack;
    @FXML
    private Button demande;

    @FXML
    private Button materiel;

    @FXML
    private AnchorPane slider;


   /* public sampleFront() {
        demandeService = new DemandeService();
    }*/

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(SessionManager.getCurrentUser().getRoles().equals("[\"ROLE_DONATEUR\"]")){
            demande.setVisible(false);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/CampagneView.fxml"));
                Parent root = loader.load();
                CampagneViewController CampagneViewController = loader.getController();
                CampagneViewController.initSampleController(this);
                contenu.getChildren().setAll(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (SessionManager.getCurrentUser().getRoles().equals("[\"ROLE_DIRECTEUR\"]")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/CampagneView.fxml"));
                Parent root = loader.load();
                CampagneViewController CampagneViewController = loader.getController();
                CampagneViewController.initSampleController(this);
                contenu.getChildren().setAll(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


       /* try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/card.fxml"));
            Parent root = loader.load();
            Controller demandeFrontController = loader.getController();
            demandeFrontController.initSampleController(this); // Passez la référence du SampleController au DemandeController
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        slider.setTranslateX(0);


        Menu.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.5));
            slide.setNode(slider);
            slide.setToX(0);
            slide.play();
            slider.setTranslateX(-176);
            slide.setOnFinished((ActionEvent e) -> {
                Menu.setVisible(false);
                MenuBack.setVisible(true);
            });
        });

        MenuBack.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.5));
            slide.setNode(slider);
            slide.setToX(-176);
            slide.play();
            slider.setTranslateX(0);
            slide.setOnFinished((ActionEvent e) -> {
                Menu.setVisible(true);
                MenuBack.setVisible(false);
            });
        });
        User currentUser = SessionManager.getCurrentUser();

        // Display the name of the current user
        if (currentUser != null) {
            userNameLabel.setText(currentUser.getNom() + " " + currentUser.getPrenom());
        }
    }
    @FXML
    protected void afficherDemande() {
        if(SessionManager.getCurrentUser().getRoles().equals("[\"ROLE_DIRECTEUR\"]")){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/card.fxml"));
            Parent root = loader.load();
            Controller demandeFrontController = loader.getController();
            demandeFrontController.initSampleController(this); // Passez la référence du SampleController au DemandeController
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
        }
    }


    @FXML
    protected void afficherActualite() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ActualiteFront.fxml"));
            Parent root = loader.load();

            ActualiteFront actualiteFront = loader.getController();

            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void afficherPersonnelsFront() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PersonnelFront.fxml"));
            Parent root = loader.load();

            PersonnelFrontController personnelFrontController = loader.getController();

            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void afficherevent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EvenementFront.fxml"));
            Parent root = loader.load();

            EvenementFront evenementFront = loader.getController();

            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    protected void afficherCategorie() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CategorieInterfaceFront.fxml"));
            Parent root = loader.load();
            CategorieInterfaceFrontController CategorieController = loader.getController();
            CategorieController.initSampleController(this);
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void afficherCampagne() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CampagneView.fxml"));
            Parent root = loader.load();
            CampagneViewController CampagneViewController = loader.getController();
CampagneViewController.initSampleController(this);
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void afficherDon() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DonView.fxml"));
            Parent root = loader.load();
            DonViewController DonViewController = loader.getController();
DonViewController.initSampleController(this);
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
       public void voirdetailMateriel(Categorie c) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MaterielInterfaceFront.fxml"));
            Parent root = loader.load();
            MaterielInterfaceFrontController materielInterfaceC = loader.getController();
            materielInterfaceC.setCategoryId(c.getId()); // Appeler la méthode pour transmettre categoryId
            materielInterfaceC.initSampleController(this);
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void ShowDonDetails(Don D) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ShowDon.fxml"));
            Parent root = loader.load();
            ShowDonController controller = loader.getController();
            controller.setDon(D);
            controller.initSampleController(this);
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void ShowCampagneDetails(Campagne C) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ShowCampagne.fxml"));
            Parent root = loader.load();
            ShowCampagneController controller = loader.getController();
            controller.setCampagne(C);
            controller.initSampleController(this);
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void PasserModifier(int id) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierDemande.fxml"));
            Parent root = loader.load();
            DemandeFrontController demandeFrontController = loader.getController();
            demandeFrontController.initData(id);
            demandeFrontController.initSampleController(this);
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void PasserDemande() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutDemandeFront.fxml"));
            Parent root = loader.load();
            DemandeFrontController DemandeFrontControllerr = loader.getController();
            DemandeFrontControllerr.initSampleController(this);
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void PasserDemandeDON(int idDon) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutDemandeFront.fxml"));
            Parent root = loader.load();
            DemandeFrontController DemandeFrontControllerr = loader.getController();
            DemandeFrontControllerr.initidDon(idDon);
            DemandeFrontControllerr.initSampleController(this);
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void afficherRendezVous() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RendezVousFront.fxml"));
            Parent root = loader.load();
            RendezVousFrontController RendezVousController = loader.getController();
         //   DemandeFrontControllerr.initSampleController(this);
            contenu.getChildren().setAll(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void RetourCategorie() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CategorieInterfaceFront.fxml"));
            Parent root = loader.load();
            CategorieInterfaceFrontController CategorieController = loader.getController();
            CategorieController.initSampleController(this);
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

            Parent root = FXMLLoader.load(getClass().getResource("/EditProfileFront.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

