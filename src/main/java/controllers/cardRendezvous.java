package controllers;


import Entity.RendezVous;
import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;

public class cardRendezvous {

    @FXML
    private Label date;

    @FXML
    private Label lieu;

    @FXML
    private Label objective;

        public void setData(RendezVous demande){
        lieu.setText(demande.getLieu());
        objective.setText(demande.getObjective());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = dateFormat.format(demande.getDate());
        date.setText(formattedDate);
        lieu.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        objective.setStyle("-fx-font-size: 12px;");
       }

    @FXML
    void TestopenMap(){
        Stage stage = new Stage();
        System.setProperty("javafx.platform", "desktop");
        System.setProperty("http.agent", "Gluon Mobile/1.0.3");
        VBox root = new VBox();
        MapView mapView = new MapView();
        try {
            OpenStreetMapGeocoding.Coordinate location = OpenStreetMapGeocoding.geocode(lieu.getText());
            if (location != null) {
                // Créer un point sur la carte avec les coordonnées géocodées
                MapPoint addressLocation = new MapPoint(location.getLatitude(), location.getLongitude());

                // Création et ajout d'une couche à la carte
                CustomCircleMarkerLayer markerLayer = new CustomCircleMarkerLayer(addressLocation);
                mapView.addLayer(markerLayer);

                // Zoom sur l'emplacement géocodé
                mapView.setZoom(17);
                mapView.flyTo(0, addressLocation, 0.1);
            } else {
                System.out.println("Adresse introuvable.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        root.getChildren().add(mapView);
        Scene scene = new Scene(root, 640, 480);
        stage.setScene(scene);
        stage.show();
    }
}
