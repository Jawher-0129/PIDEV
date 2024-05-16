package controllers;

import Entity.Campagne;
import Entity.Don;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import service.DonService;
import service.CampagneService;

import java.time.LocalDate;
import java.util.List;
import javafx.util.StringConverter;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import service.EmailService;

public class DonFormController {

    @FXML private Label labelTitre;
    @FXML private TextField champType;
    @FXML private TextField champMontant;
    @FXML private DatePicker selecteurDateRemise;
    @FXML private ComboBox<Campagne> comboCampagne;
    @FXML private Button boutonEnregistrer;

    @FXML private Label typeError, montantError, dateRemiseError, campagneError;  // Labels for displaying validation errors

    private DonService serviceDon = new DonService();
    private CampagneService serviceCampagne = new CampagneService();
    private EmailService emailService = new EmailService();
    private Don donActuel;

    public void initialize() {
        updateFormTitle();
        chargerCampagnes();
        boutonEnregistrer.setOnAction(event -> gererEnregistrementDon());
    }

    public void setDon(Don don) {
        donActuel = don;
        updateFormTitle();
        if (don != null) {
            champType.setText(don.getType());
            champMontant.setText(don.getMontant() != null ? don.getMontant().toString() : "");
            selecteurDateRemise.setValue(LocalDate.parse(don.getDate_remise()));
            comboCampagne.setValue(findCampagneById(don.getCampagne_id()));
        } else {
            clearForm();
        }
    }

    private void updateFormTitle() {
        if (donActuel == null) {
            labelTitre.setText("Ajouter un Nouveau Don");
        } else {
            labelTitre.setText("Modifier le Don");
        }
    }

    private Campagne findCampagneById(Integer campagneId) {
        return campagneId == null ? null : serviceCampagne.findById(campagneId).orElse(null);
    }

    private void chargerCampagnes() {
        List<Campagne> campagnes = serviceCampagne.findAll();
        comboCampagne.setItems(FXCollections.observableArrayList(campagnes));
        comboCampagne.setConverter(new StringConverter<Campagne>() {
            @Override
            public String toString(Campagne campagne) {
                return campagne == null ? null : campagne.getTitre();
            }
            @Override
            public Campagne fromString(String string) {
                return null;
            }
        });
    }

    private void gererEnregistrementDon() {
        if (validerSaisie()) {
            String type = champType.getText();
            Integer montant = essayerParserInt(champMontant.getText());
            LocalDate dateRemise = selecteurDateRemise.getValue();
            Campagne selectedCampagne = comboCampagne.getSelectionModel().getSelectedItem();

            if (donActuel == null) {
               // donActuel = new Don(type, montant, dateRemise.toString(), selectedCampagne != null ? selectedCampagne.getId() : null);
                donActuel = new Don( selectedCampagne != null ? selectedCampagne.getId() : null,dateRemise.toString(), SessionManager.getCurrentUser().getId(), montant,type );
                Don donEnregistre = serviceDon.save(donActuel);
                if (donEnregistre != null) {
                    afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Don ajouté avec succès.");
                    try {
                        emailService.sendDonationConfirmationEmail(SessionManager.getCurrentUser().getEmail(), "donor");  // Replace with dynamic recipient info if available
                        System.out.println("Confirmation email sent successfully.");
                    } catch (MailjetException | MailjetSocketTimeoutException e) {
                        System.err.println("Failed to send confirmation email: " + e.getMessage());
                    }
                } else {
                    afficherAlerte(Alert.AlertType.ERROR, "Erreur", "L'ajout du don a échoué.");
                }
            } else {
                donActuel.setType(type);
                donActuel.setMontant(montant);
                donActuel.setDate_remise(dateRemise.toString());
                donActuel.setCampagne_id(selectedCampagne != null ? selectedCampagne.getId() : null);

                Don donMisAJour = serviceDon.update(donActuel);
                if (donMisAJour != null) {
                    afficherAlerte(Alert.AlertType.INFORMATION, "Succès", "Don mis à jour avec succès.");
                    try {
                        emailService.sendDonationConfirmationEmail(SessionManager.getCurrentUser().getEmail(), "Donor");  // Replace with dynamic recipient info if available
                        System.out.println("Confirmation email sent successfully.");
                    } catch (MailjetException | MailjetSocketTimeoutException e) {
                        System.err.println("Failed to send confirmation email: " + e.getMessage());
                    }
                } else {
                    afficherAlerte(Alert.AlertType.ERROR, "Erreur", "La mise à jour du don a échoué.");
                }
            }
        }
    }


    private boolean validerSaisie() {
        boolean isValid = true;
        clearErrors();

        if (champType.getText().isEmpty()) {
            typeError.setText("Le type ne peut pas être vide.");
            champType.getStyleClass().add("error");
            isValid = false;
        } else {
            champType.getStyleClass().remove("error");
            typeError.setText("");
        }

        if (selecteurDateRemise.getValue() == null) {
            dateRemiseError.setText("La date de remise est requise.");
            selecteurDateRemise.getStyleClass().add("error");
            isValid = false;
        } else if (selecteurDateRemise.getValue().isBefore(LocalDate.now())) {
            dateRemiseError.setText("La date de remise doit être dans le futur.");
            selecteurDateRemise.getStyleClass().add("error");
            isValid = false;
        } else {
            dateRemiseError.setText("");
        }

        if (!champMontant.getText().isEmpty()) {
            Integer montant = essayerParserInt(champMontant.getText());
            if (montant == null || montant <= 0) {
                montantError.setText("Le montant doit être positif si fourni.");
                champMontant.getStyleClass().add("error");
                isValid = false;
            } else {
                champMontant.getStyleClass().remove("error");
                montantError.setText("");
            }
        }

        return isValid;
    }

    private void clearErrors() {
        typeError.setText("");
        montantError.setText("");
        dateRemiseError.setText("");
        campagneError.setText("");

        champType.getStyleClass().remove("error");
        champMontant.getStyleClass().remove("error");
        selecteurDateRemise.getStyleClass().remove("error");
        comboCampagne.getStyleClass().remove("error");
    }

    private Integer essayerParserInt(String texte) {
        try {
            return Integer.parseInt(texte);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void afficherAlerte(Alert.AlertType typeAlerte, String titre, String contenu) {
        Alert alerte = new Alert(typeAlerte);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.setContentText(contenu);
        alerte.showAndWait();
    }

    private void clearForm() {
        champType.clear();
        champMontant.clear();
        selecteurDateRemise.setValue(null);
        comboCampagne.getSelectionModel().clearSelection();
    }
}