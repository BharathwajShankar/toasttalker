package com.d121toastmaster.bot.ToastTalker.service;

import com.d121toastmaster.bot.ToastTalker.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class JugalbandiService {

    @Autowired
    RestTemplate restTemplate;
    @Value("${jugalbandi.api.url}")
    private String jugalbandiApiUrl;
    @Autowired
    private UserService userService;
    @Autowired
    private ToastTalkService toastTalkService;

    public JugalbandiService() {
    }

    private static String extractRephrasedQuery(String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            JsonNode rephrasedQueryNode = jsonNode.get("rephrased_query");
            if (rephrasedQueryNode != null) {
                return rephrasedQueryNode.asText();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Object> queryJugalbandi(String juuid, String tuuid, String outputFormat, String queryText)
            throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String inputLanguage = userService.findById(tuuid).map(User::getLanguage).get().trim();
        if (isEmptyJuuid(juuid)) {
            return toastTalkService.search(queryText, outputFormat, inputLanguage, tuuid);
        } else {
            Map<String, Object> responseMap = new HashMap<>();
            String apiUrl = jugalbandiApiUrl + "/query-using-voice-gpt4" +
                    "?input_language=" + inputLanguage +
                    "&output_format=" + outputFormat +
                    "&query_text=" + queryText +
                    "&uuid_number=" + juuid;

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String answer = jsonNode.get("answer").asText();
            responseMap.put("juuid", juuid);
            responseMap.put("response", answer);
            return responseMap;
        }
    }

    private boolean isEmptyJuuid(String juuid) {
        return juuid == null || juuid.equals("null") || juuid.trim().isEmpty() || juuid.equals("\"\"");
    }

    public String healthCheckJugalbandi() {
        String apiUrl = jugalbandiApiUrl;
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
        return response.getBody();
    }

    public String rephraseQuery(String query) {
        String apiUrl = jugalbandiApiUrl + "rephrased-query?query_string=" + query;
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return extractRephrasedQuery(Objects.requireNonNull(response.getBody()));
        } else {
            return "Failed to retrieve data from the API.";
        }
    }
}
