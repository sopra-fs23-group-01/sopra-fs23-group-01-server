package ch.uzh.ifi.hase.soprafs23.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ChatService {

    private List<String> words;

    public ChatService() {
        try {
            words = getWordsRelatedTo("sport");
        } catch (IOException e) {
            e.printStackTrace();
            words = Arrays.asList("bike", "banana", "cherry", "orange", "grape");
        }
    }

    public String getRandomWord() {
        return words.get(new Random().nextInt(words.size()));
    }

    private List<String> getWordsRelatedTo(String query) throws IOException {
        List<String> words = new ArrayList<>();
        String apiUrl = "https://api.datamuse.com/words?ml=" + query;
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                JsonNode jsonNode = objectMapper.readTree(connection.getInputStream());
                for (JsonNode wordNode : jsonNode) {
                    words.add(wordNode.get("word").asText());
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return words;
    }}
