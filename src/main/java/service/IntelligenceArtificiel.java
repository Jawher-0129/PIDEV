package service;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
public class IntelligenceArtificiel {
    public static String analyzeAnswersSentiment(List<String> formAnswerStrings) {
        try {
            String apiKey = "e674640f89msh7db50bcfd2039a6p182705jsn50541a545b9f"; // Your OpenAI API key
            String endpoint = "open-ai21.p.rapidapi.com"; // Adjust the endpoint

            // Construct the prompt with user answers
            JSONArray messagesArray = new JSONArray();
            for (String answer : formAnswerStrings) {
                JSONObject messageObject = new JSONObject();
                messageObject.put("role", "user");
                messageObject.put("content", answer);
                messagesArray.put(messageObject);
            }

            // Construct the request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("messages", messagesArray);
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("prompt", "Calculate the percentage of satisfaction based on the sentiment of the responses.");

            // Send the request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://" + endpoint + "/conversationgpt35"))
                    .header("content-type", "application/json")
                    .header("X-RapidAPI-Key", apiKey)
                    .header("X-RapidAPI-Host", endpoint)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Process the response
            if (response.statusCode() == 200) {
                return processResponse(response.body());
            } else {
                System.err.println("Error: HTTP " + response.statusCode() + " - " + response.body());
                return null; // Ou traiter la réponse d'erreur en conséquence
            }
        } catch (IOException | InterruptedException | JSONException e) {
            e.printStackTrace();
            return null; // Ou traiter l'exception en conséquence
        }
    }

    private static String processResponse(String responseBody) {
        // Ajoutez votre logique de traitement ici
        System.out.println("Réponse de GPT-3.5:\n" + responseBody);
        return responseBody;
    }
}

