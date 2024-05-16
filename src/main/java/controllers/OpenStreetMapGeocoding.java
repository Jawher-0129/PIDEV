package controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class OpenStreetMapGeocoding {

    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";

    public static Coordinate geocode(String query) throws Exception {
        // Encode the query to make it URL safe
        String encodedQuery = URLEncoder.encode(query, "UTF-8");

        // Construct the URL for the Nominatim API request
        String urlString = NOMINATIM_URL + "?format=json&q=" + encodedQuery;

        // Send the HTTP request
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Read the response
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        // Parse the JSON response
        JSONParser parser = new JSONParser();
        JSONArray jsonResponse = (JSONArray) parser.parse(response.toString());

        // Extract the coordinates from the JSON response
        if (!jsonResponse.isEmpty()) {
            JSONObject result = (JSONObject) jsonResponse.get(0);
            double lat = Double.parseDouble((String) result.get("lat"));
            double lon = Double.parseDouble((String) result.get("lon"));
            return new Coordinate(lat, lon);
        }

        return null;
    }

    public static class Coordinate {
        private double latitude;
        private double longitude;

        public Coordinate(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }

    public static void main(String[] args) {
        try {
            // Géocoder "Tunis"
            String query = "Tunis";
            Coordinate coordinate = geocode(query);
            if (coordinate != null) {
                System.out.println("Coordonnées de " + query + ":");
                System.out.println("Latitude: " + coordinate.getLatitude());
                System.out.println("Longitude: " + coordinate.getLongitude());
            } else {
                System.out.println("Adresse introuvable.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
