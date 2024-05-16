package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.IOException;
import javafx.scene.control.ButtonType;
import javafx.geometry.Insets;
import java.util.Optional;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.scene.image.Image;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javafx.scene.image.ImageView;
import Entity.Campagne;
import Entity.Don;
import service.DonService;

public class ShowDonController {

    @FXML
    private VBox vbox;  // Ensure this matches fx:id in FXML
    @FXML
    private Label typeLabel, amountLabel, dateLabel;
    @FXML
    private Button updateButton, deleteButton;
    @FXML
    private Button PasserDemande;
    private Don currentDon;
    private DonService donService = new DonService();

    @FXML
    private ImageView qrCodeImage;
    private sampleFront sampleFront;

    public void initSampleController(sampleFront controller) {
        sampleFront = controller;
    }

    @FXML
    public void initialize() {
        // Set padding programmatically to avoid FXML load issues
        vbox.setPadding(new Insets(20, 20, 20, 20));

    }
    public void setDon(Don don) {
        currentDon = don;
        if (SessionManager.getCurrentUser().getRoles().equals("[\"ROLE_DIRECTEUR\"]") ){
            if (currentDon.getCampagne_id() == null){
                PasserDemande.setVisible(false);
            }
            updateButton.setVisible(false);
            deleteButton.setVisible(false);
        }
        if (SessionManager.getCurrentUser().getRoles().equals("[\"ROLE_DONATEUR\"]") ){
            PasserDemande.setVisible(false);
            if (currentDon.getDonateur() != SessionManager.getCurrentUser().getId()){
                updateButton.setVisible(false);
                deleteButton.setVisible(false);
            }
        }

        typeLabel.setText("Type: " + don.getType());
        amountLabel.setText("Montant: " + (don.getMontant() == null ? "N/A" : don.getMontant() + " €"));
        dateLabel.setText("Date de remise: " + don.getDate_remise());
        updateButton.setOnAction(e -> showDonForm(don));
        deleteButton.setOnAction(e -> deleteDon(don.getId()));
        generateAndDisplayQRCode(don);
    }


    private void generateAndDisplayQRCode(Don don) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            String data = "Don ID: " + don.getId() + ", Type: " + don.getType() + ", Montant: " + don.getMontant() + ", Date: " + don.getDate_remise();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);

            BufferedImage bufferedImage = createBufferedImage(bitMatrix);
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            qrCodeImage.setImage(image);
        } catch (WriterException e) {
            e.printStackTrace(); // Proper error handling should be added here
        }
    }

    private BufferedImage createBufferedImage(BitMatrix bitMatrix) {
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return image;
    }

    private void showDonForm(Don don) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DonForm.fxml"));
            VBox form = loader.load();
            DonFormController controller = loader.getController();
            controller.setDon(don);

            Stage stage = new Stage();
            stage.setScene(new Scene(form));
            stage.setTitle("Modifier le Don");
            stage.showAndWait();

            // Refresh data display after the form is closed
            refreshDonDisplay();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void refreshDonDisplay() {
        if (currentDon != null) {
            // Assuming DonService or similar method to fetch the latest data
            Optional<Don> optionalDon = donService.findById(currentDon.getId());
            if (optionalDon.isPresent()) {
                Don updatedDon = optionalDon.get();
                setDon(updatedDon); // Reuse the setDon method to update the UI
            }
        }
    }


    @FXML
    private void handleBack() {
        initialize(); // Ensure vbox is initialized
        if (vbox != null && vbox.getScene() != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/DonView.fxml"));
                Parent previousPage = loader.load();
                vbox.getScene().setRoot(previousPage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Cannot navigate back because vbox is not attached to any scene.");
        }
    }




    private void deleteDon(int id) {
        Alert confirmation = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete this donation?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    donService.deleteById(id);
                    // Navigate back to DonView after successful deletion
                    handleBack();
                } catch (Exception ex) {
                    Alert errorAlert = new Alert(AlertType.ERROR, "Error deleting donation: " + ex.getMessage());
                    errorAlert.show();
                }
            }
        });
    }

    @FXML
    void PasserDemande(ActionEvent event) {
      //  sampleFront.PasserDemande();
        sampleFront.PasserDemandeDON(currentDon.getId());
    }

}
