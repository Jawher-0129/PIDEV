package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import Entity.Campagne;
import Entity.Don;
import service.CampagneService;
import javafx.geometry.Insets;
import java.util.Optional;
import javafx.scene.Parent;
import javafx.stage.Modality;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

public class ShowCampagneController {

    @FXML private VBox detailsBox;
    @FXML
    private Button donatenowbutton;
    @FXML private Label titleLabel, titleValue, descriptionLabel, descriptionValue, startDateLabel, startDateValue, endDateLabel, endDateValue;
    @FXML private ImageView imageView ,qrCodeImage;
    @FXML private Button modifyButton, deleteButton;
    @FXML private Button downloadPdfButton;

    private CampagneService service = new CampagneService();
    private Campagne currentCampagne;
    private sampleFront sampleFront;

    public void initSampleController(sampleFront controller) {
        sampleFront = controller;
    }
    @FXML
    public void initialize() {    detailsBox.setPadding(new Insets(20));
        modifyButton.setVisible(true);
        deleteButton.setVisible(true);
        if(SessionManager.getCurrentUser().getRoles().equals("[\"ROLE_DONATEUR\"]")){
            modifyButton.setVisible(false);
            deleteButton.setVisible(false);
        }

    }
    public void setCampagne(Campagne campagne) {
        currentCampagne = campagne;
        if(SessionManager.getCurrentUser().getRoles().equals("[\"ROLE_DIRECTEUR\"]")){
            if(SessionManager.getCurrentUser().getId() != currentCampagne.getDirecteur_id())
            {
                modifyButton.setVisible(false);
                deleteButton.setVisible(false);
            }
            donatenowbutton.setVisible(false);
        }
        titleValue.setText(campagne.getTitre());
        descriptionValue.setText(campagne.getDescription());
        startDateValue.setText(campagne.getDate_debut());
        endDateValue.setText(campagne.getDate_fin());
        if (campagne.getImage() != null && !campagne.getImage().isEmpty()) {
            imageView.setImage(new Image("file:" + campagne.getImage()));
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);
        }
        generateAndDisplayQRCode();


    }



    private void generateAndDisplayQRCode() {
        try {
            String qrData = String.format("Title: %s\nDescription: %s\nStart: %s\nEnd: %s",
                    currentCampagne.getTitre(), currentCampagne.getDescription(), currentCampagne.getDate_debut(), currentCampagne.getDate_fin());

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrData, BarcodeFormat.QR_CODE, 200, 200);

            BufferedImage bufferedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < 200; y++) {
                for (int x = 0; x < 200; x++) {
                    bufferedImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", os);
            Image qrcode = new Image(new ByteArrayInputStream(os.toByteArray()));
            qrCodeImage.setImage(qrcode);
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void handleBack() {
        // Logic to navigate back goes here
        // For example, if you want to set the scene's root to the previous page
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CampagneView.fxml")); // replace with your actual previous page FXML
            Parent previousPage = loader.load();
            detailsBox.getScene().setRoot(previousPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openDonationForm() {
        try {
            // Charger le FXML du formulaire de don
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/DonForm.fxml"));
            Parent root = fxmlLoader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Créer une nouvelle fenêtre de popup
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setScene(scene);
            popupStage.setTitle("Formulaire de Don");

            // Afficher la fenêtre de popup
            popupStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleModify() {
        if (currentCampagne != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/CampagneForm.fxml"));
                ScrollPane root = (ScrollPane) loader.load();  // Cast to ScrollPane instead of VBox
                CampagneFormController controller = loader.getController();
                controller.setCampagne(currentCampagne);

                Stage stage = new Stage();
                stage.setScene(new Scene(root)); // Set the scene with ScrollPane
                stage.setTitle("Modifier la Campagne");
                stage.showAndWait(); // Wait for the form to close

                setCampagne(currentCampagne); // Optionally refresh the displayed campagne details
            } catch (IOException e) {
                e.printStackTrace();
                Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Error opening the edit form: " + e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }

    @FXML
    private void handleDelete() {
        if (currentCampagne != null && currentCampagne.getId() > 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this campaign?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                service.deleteById(currentCampagne.getId());
                handleBack(); // Call handleBack after deletion
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No valid campaign is loaded for deletion.");
            alert.showAndWait();
        }
    }


    private void closeStage() {
        Stage stage = (Stage) detailsBox.getScene().getWindow();
        stage.close();
    }

}
