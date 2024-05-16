package controllers;
import Entity.Personnel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.geometry.Pos;
import javafx.animation.TranslateTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.animation.Timeline;
import javafx.util.Duration;

import javafx.scene.text.Text;
import service.PersonnelService;

public class PersonnelFrontController implements Initializable {

    private final PersonnelService personnelService = new PersonnelService();
    // Méthode pour appeler l'API de traduction et obtenir la traduction pour le texte et la langue cible
    private String callTranslationAPI(String text, String targetLanguage) {
        // Ici, vous devez appeler l'API de traduction pour traduire le texte vers la langue cible
        // Vous pouvez utiliser la bibliothèque Google Cloud Translate ou toute autre API de traduction de votre choix
        // Cette méthode devrait retourner le texte traduit
        // Exemple d'utilisation de la bibliothèque Google Cloud Translate (vous devez configurer votre projet pour utiliser Google Cloud Translate et avoir les autorisations nécessaires) :
    /*
    Translate translate = TranslateOptions.getDefaultInstance().getService();
    Translation translation = translate.translate(text, Translate.TranslateOption.targetLanguage(targetLanguage));
    return translation.getTranslatedText();
    */
        // Pour cet exemple, je vais simplement retourner un texte factice
        return "Texte traduit vers " + targetLanguage + ": " + text;
    }

    // Méthode pour mettre à jour l'interface utilisateur avec la traduction
    private void updateUIWithTranslation(String originalText, String targetLanguage, String translatedText) {
        // Ici, vous devez mettre à jour l'interface utilisateur avec le texte traduit
        // Par exemple, vous pouvez rechercher toutes les étiquettes contenant le texte original
        // et les remplacer par le texte traduit
        // Voici un exemple simplifié :
    /*
    for (Node node : rootPane.getChildren()) {
        if (node instanceof Label) {
            Label label = (Label) node;
            if (label.getText().equals(originalText)) {
                label.setText(translatedText);
            }
        }
    }
    */
        // Pour cet exemple, je vais simplement imprimer le texte traduit
        System.out.println("Texte traduit vers " + targetLanguage + ": " + translatedText);
    }


    @FXML
    private ScrollPane NosPersonnelsScrollPane;

    @FXML
    private ScrollPane TopPersonnelScrollPane;

    @FXML
    private HBox TopPersonnelsBox;

    @FXML
    private TilePane cardsContainer;

    @FXML
    private VBox rootPane;
    private service.PersonnelService PersonnelService = new PersonnelService();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadPersonnels();
        loadTopPersonnels();
        animateTopPersonnels();
        // Appel de la méthode pour récupérer tous les textes à traduire
        List<String> textsToTranslate = getAllTextsToTranslate(rootPane);
        // Utilisation des textes récupérés, par exemple, en les imprimant
        for (String text : textsToTranslate) {
            System.out.println(text);
        }
    }

    // Méthode récursive pour récupérer tous les textes à traduire dans les nœuds enfants
    private List<String> getAllTextsToTranslate(Node node) {
        List<String> textsToTranslate = new ArrayList<>();
        if (node instanceof Label) {
            Label label = (Label) node;
            textsToTranslate.add(label.getText());
        } else if (node instanceof Button) {
            Button button = (Button) node;
            textsToTranslate.add(button.getText());
        } else if (node instanceof Text) {
            Text textNode = (Text) node;
            textsToTranslate.add(textNode.getText());
        }
        if (node instanceof VBox || node instanceof HBox) {
            for (Node child : ((VBox) node).getChildren()) {
                textsToTranslate.addAll(getAllTextsToTranslate(child));
            }
        }
        return textsToTranslate;
    }


    private void loadPersonnels() {
        for (Personnel personnel : personnelService.getAll()) {
            if(personnel.getRating()<5) {
                VBox card = createPersonnelCard(personnel);
                cardsContainer.getChildren().add(card);
            }

        }
    }



    private VBox createPersonnelCard(Personnel personnel) {
        VBox card = new VBox(10);
        card.getStyleClass().add("personnel-card");





        try {
            String imageurl;
            if(!(personnel.getImage().startsWith("C")))
            {
                imageurl="C:\\Users\\jawhe\\OneDrive\\Bureau\\3A32HealthSwiftIntegration (2)\\3A32HealthSwiftIntegration\\3A32HealthSwift\\public\\uploads\\Images\\"+personnel.getImage();
                System.out.println(imageurl);
            }
            else
                imageurl=personnel.getImage();

            ImageView imageView = new ImageView(new Image(new FileInputStream(imageurl)));
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            card.getChildren().add(imageView);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }



        Label nomLabel = new Label("Nom: " + personnel.getNom());
        nomLabel.getStyleClass().add("personnel-nom");
        card.getChildren().add(nomLabel);

        Label prenomLabel = new Label("Prenom: " + personnel.getPrenom_personnel());
        prenomLabel.getStyleClass().add("personnel-prenom");
        card.getChildren().add(prenomLabel);


        Label ratingLabel = new Label("Rating: " + personnel.getRating());
        ratingLabel.getStyleClass().add("personnel-rating");
        card.getChildren().add(ratingLabel);

        HBox ratingStars = createRatingStars(personnel.getRating());
        card.getChildren().add(ratingStars);

        card.setOnMouseClicked(Personnel -> {
            if (Personnel.getClickCount() == 2) {
                // Show full details in popup
                showDetailsPersonnel(personnel);
            }
        });

        return card;
    }







    private void showDetailsPersonnel(Personnel personnel) {
        // Create VBox to hold all elements
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));



        // Image
        try {
            ImageView imageView = new ImageView(new Image(new FileInputStream(personnel.getImage())));
            imageView.setFitWidth(200);
            imageView.setFitHeight(200);
            HBox image = new HBox(imageView);
            image.setAlignment(Pos.CENTER);
            vbox.getChildren().add(image);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Details labels
        Label nomLabel = new Label("Nom: " + personnel.getNom());
        Label prenomLabel = new Label("Prenom: " + personnel.getPrenom_personnel());
        Label ratingLabel = new Label("Rating: " + personnel.getRating());
        Label roleLabel = new Label("Role: " + personnel.getRole());


        // Add details labels to VBox
        vbox.getChildren().addAll(nomLabel, prenomLabel, ratingLabel, roleLabel);

        // Create and configure the stage for the popup
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Personnel Details");

        // Create scene and set it to the stage
        Scene scene = new Scene(vbox, 400, 300);
        popupStage.setScene(scene);


        popupStage.show();

    }
    private void loadTopPersonnels() {
        if (TopPersonnelsBox != null) {
            List<Personnel> topPersonnels = personnelService.getTopPersonnels(5);
            for (Personnel personnel : topPersonnels) {
                VBox card = createNosPersonnelCard(personnel); // Create a simple personnel card
                TopPersonnelsBox.getChildren().add(card);
            }
        } else {
            System.out.println("topBox is null");
        }
    }
    private VBox createNosPersonnelCard(Personnel personnel) {
        VBox card = new VBox(10);
        card.getStyleClass().add("personnel-card");


        Label nomLabel = new Label("Nom: " + personnel.getNom());
        nomLabel.getStyleClass().add("personnel-nom");
        card.getChildren().add(nomLabel);


        Label prenomLabel = new Label("Prenom: " + personnel.getPrenom_personnel());
        prenomLabel.getStyleClass().add("personnel-prenom");
        card.getChildren().add(prenomLabel);


        Label ratingLabel = new Label("Rating: " + personnel.getRating());
        ratingLabel.getStyleClass().add("personnel-rating");
        card.getChildren().add(ratingLabel);


        // Appel à la méthode createRatingStars pour générer les étoiles de rating
        HBox ratingStars = createRatingStars(personnel.getRating());
        card.getChildren().add(ratingStars);

        ImageView imageView = new ImageView();
        try {
            String imageurl;
            if(!(personnel.getImage().startsWith("C")))
            {
                imageurl="C:\\Users\\jawhe\\OneDrive\\Bureau\\3A32HealthSwiftIntegration (2)\\3A32HealthSwiftIntegration\\3A32HealthSwift\\public\\uploads\\Images"+personnel.getImage();
                System.out.println(imageurl);
            }
            else
                imageurl=personnel.getImage();
            Image image = new Image(imageurl);
            imageView.setImage(image);
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            card.getChildren().add(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Add double-click personnel handler
        card.setOnMouseClicked(Personnel -> {
            if (Personnel.getClickCount() == 2) {
                // Show full details in popup
                showDetailsPersonnel(personnel);
            }
        });

        return card;

    }
    // Méthode pour générer une image d'étoile dorée en fonction du rating
    // Dans votre méthode createRatingStars
    private HBox createRatingStars(int rating) {
        HBox starsBox = new HBox(5);
        int maxRating = 5; // Nombre maximum d'étoiles
        for (int i = 0; i < maxRating; i++) {
            ImageView starImageView = new ImageView();
            if (i < rating) {
                // Si l'index est inférieur au rating, affichez une étoile dorée
                starImageView.setImage(new Image("C:\\Users\\jawhe\\OneDrive\\Bureau\\3A32HealthSwiftIntegration (2)\\3A32HealthSwiftIntegration\\3A32HealthSwift\\public\\uploads\\etoile.jpeg")); // Remplacez par le chemin de votre image d'étoile dorée
            } else {
                // Sinon, affichez une étoile vide ou grise
                starImageView.setImage(new Image("C:\\Users\\jawhe\\OneDrive\\Bureau\\3A32HealthSwiftIntegration (2)\\3A32HealthSwiftIntegration\\3A32HealthSwift\\public\\uploads\\etoilevide.jpeg")); // Remplacez par le chemin de votre image d'étoile vide ou grise
            }
            starImageView.setFitWidth(20); // Ajustez la largeur de l'étoile selon vos préférences
            starImageView.setFitHeight(20); // Ajustez la hauteur de l'étoile selon vos préférences
            starsBox.getChildren().add(starImageView);
        }
        return starsBox;
    }

    private void animateTopPersonnels() {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(10), TopPersonnelsBox);
        transition.setToX(-200);
        transition.setCycleCount(TranslateTransition.INDEFINITE);
        transition.play();
    }

    // Définir une liste de toutes les langues cibles vers lesquelles vous souhaitez traduire votre contenu
    List<String> allTargetLanguages = Arrays.asList("en", "fr", "es", "de");

    @FXML
    private void traduireContenu() {
        // Récupérer tous les textes à traduire sur la page
        List<String> textsToTranslate = getAllTextsToTranslate(rootPane);

        // Parcourir chaque texte à traduire
        for (String text : textsToTranslate) {
            // Appeler l'API de traduction pour chaque texte dans toutes les langues cibles souhaitées
            for (String targetLanguage : allTargetLanguages) {
                // Appeler l'API de traduction et obtenir la traduction pour le texte et la langue cible
                String translatedText = callTranslationAPI(text, targetLanguage);

                // Mettre à jour l'interface utilisateur avec la traduction
                updateUIWithTranslation(text, targetLanguage, translatedText);
            }
        }
    }

}
