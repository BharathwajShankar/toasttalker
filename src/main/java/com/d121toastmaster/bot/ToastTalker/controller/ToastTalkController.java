package com.d121toastmaster.bot.ToastTalker.controller;

import com.d121toastmaster.bot.ToastTalker.model.User;
import com.d121toastmaster.bot.ToastTalker.model.UserChatHistory;
import com.d121toastmaster.bot.ToastTalker.service.JugalbandiService;
import com.d121toastmaster.bot.ToastTalker.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@RestController
@CrossOrigin("*")
@Slf4j
public class ToastTalkController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JugalbandiService jugalbandiService;

    @Autowired
    private UserService userCreateService;

    @GetMapping("/healthCheck")
    public ResponseEntity<String> fetchData() {
        String responseBody = jugalbandiService.healthCheckJugalbandi();
        System.out.println(responseBody);
        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/query")
    public Map<String, Object> queryJugalbandi(@RequestParam String query,@RequestParam String tuuid)
            throws JsonProcessingException {
        String output = "Text";
        String juuid = "1e9e4b6c-c0da-11ee-ba70-42004e494300";
        return jugalbandiService.queryJugalbandi(juuid, tuuid, output, query);
    }

    @PostMapping("/createUserSession")
    public ResponseEntity<User> createUserSession(@RequestBody Map<String, String> requestData) {
        String userName = requestData.get("userName");
        String language = requestData.get("language");
        log.info("Received Citizen Registration Request. Name: " + userName + ". Language:" + language);
        User user = userCreateService.createCitizen(userName, language);
        log.info("User created........");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @PostMapping("/searchChatHistory")
    public Object searchChatHistory(@RequestParam String uuid) {
        log.info("Received Citizen Search History Request. Name:  " + uuid);
        return userCreateService.searchHistory(uuid);
    }

    @PostMapping("/save")
    public UserChatHistory saveChat(@RequestBody UserChatHistory chatHistory) {
        return userCreateService.saveChat(chatHistory);
    }
}


