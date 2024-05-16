package Entity;

import javafx.scene.paint.Paint;

import java.time.ZonedDateTime;

public class CalendarActivity {
    private ZonedDateTime date;
    private String clientName;
    private Integer serviceNo;
    private String color;

    public CalendarActivity(ZonedDateTime date, String clientName, Integer serviceNo) {
        this.date = date;
        this.clientName = clientName;
        this.serviceNo = serviceNo;
        this.color = color;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getClientName() {
        return clientName;
    }
    public String getDisplayText() {
        // Ajoutez ici la logique pour générer le texte d'affichage souhaité
        return "Texte d'affichage personnalisé";
    }
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getColor() {
        // Ajoutez ici la logique pour déterminer la couleur appropriée
        return "#FF0000"; // Exemple : couleur rouge représentée par une chaîne de caractères
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getServiceNo() {
        return serviceNo;
    }

    public void setServiceNo(Integer serviceNo) {
        this.serviceNo = serviceNo;
    }

    @Override
    public String toString() {
        return "CalenderActivity{" +
                "date=" + date +
                ", clientName='" + clientName + '\'' +
                ", serviceNo=" + serviceNo +
                '}';
    }
}
