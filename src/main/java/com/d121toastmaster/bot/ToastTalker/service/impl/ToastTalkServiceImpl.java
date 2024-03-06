package com.d121toastmaster.bot.ToastTalker.service.impl;

import com.d121toastmaster.bot.ToastTalker.model.User;
import com.d121toastmaster.bot.ToastTalker.service.JugalbandiService;
import com.d121toastmaster.bot.ToastTalker.service.ToastTalkService;
import com.d121toastmaster.bot.ToastTalker.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
public class ToastTalkServiceImpl implements ToastTalkService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private JugalbandiService jugalbandiService;
    @Autowired
    private UserService userService;

    @Override
    public Map<String, Object> search(String query, String output, String inputLanguage, String tuuid) {
        Map<String, Object> responseMessage = new HashMap<>();
        try {
                String uuidNumber = responseMessage.get("juuid").toString();
                Optional<User> user = userService.findById(tuuid);
                if (user.isPresent()) {
                    responseMessage.put("juuid", uuidNumber);
                    responseMessage.put("message", jugalbandiService.queryJugalbandi(uuidNumber,
                            user.map(User::getLanguage).get(), output, query));
                } else {
                    responseMessage.put("juuid", null);
                    responseMessage.put("message", jugalbandiService.queryJugalbandi(uuidNumber,
                            user.map(User::getLanguage).get(), output, query));
                }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseMessage;
    }
}
