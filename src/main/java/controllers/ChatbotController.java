package controllers;


import service.IntelligenceArtificiel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

public class ChatbotController {
    private final IntelligenceArtificiel chatbot = new IntelligenceArtificiel();
    @FXML
    private TextArea chatArea;

    @FXML
    private TextField userInputField;

    @FXML
    private Button sendButton;

    public void initialize() {
        sendButton.setOnAction(e -> sendMessage());
    }

    private void sendMessage() {
        String userInput = userInputField.getText().trim();
        if (!userInput.isEmpty()) {
            chatArea.appendText("You: " + userInput + "\n");
            List<String> userInputList = new ArrayList<>();
            userInputList.add(userInput);
            String response = IntelligenceArtificiel.analyzeAnswersSentiment(userInputList);
            displayResponse(response); // Afficher la réponse du chatbot dans l'interface graphique
            userInputField.clear();
        }
    }

    public void displayResponse(String response) {
        chatArea.appendText("Bot: " + response + "\n");
    }

}