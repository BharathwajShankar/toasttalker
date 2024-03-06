package com.d121toastmaster.bot.ToastTalker.service;

import com.d121toastmaster.bot.ToastTalker.Exception.CustomException;
import com.d121toastmaster.bot.ToastTalker.Exception.UserNameNotValidException;
import com.d121toastmaster.bot.ToastTalker.model.History;
import com.d121toastmaster.bot.ToastTalker.model.User;
import com.d121toastmaster.bot.ToastTalker.model.UserChatHistory;
import com.d121toastmaster.bot.ToastTalker.repository.ElasticSearchRepository;
import com.d121toastmaster.bot.ToastTalker.repository.ToastRepository;
import com.d121toastmaster.bot.ToastTalker.repository.ToastTalkChatRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;

import static java.util.Objects.isNull;

@Service
@Slf4j
public class UserService {

    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

    @Autowired
    private ToastRepository toastRepository;

    @Autowired
    private ToastTalkChatRepository toastTalkChatRepository;

    private ObjectMapper mapper;

    @Autowired
    public UserService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public User save(User entity) {
        return toastRepository.save(entity);
    }
    public Optional<User> findById(String uuid) {
        return toastRepository.findById(uuid);
    }
    /*public List<User> findByUserName(String userName) {
        return yatriRepository.findByUserName(userName);
    }*/

    public User createCitizen(String userName, String language) {
        validateAndEnrichCitizen(userName, language);
        return createUser(userName, language);
    }

    private void validateAndEnrichCitizen(String userName, String language) {
        log.info("Validating User........");
        if (org.springframework.util.StringUtils.isEmpty(userName))
            throw new UserNameNotValidException();
    }

    public User createUser(String userName, String language) {
        User user = new User();
        user.setUsername(userName);
        user.setLanguage(language);
        user.setUuid(UUID.randomUUID().toString());
        return save(user);
    }

    public UserChatHistory UpdateChatHistory(UserChatHistory userChatHistory) {
        return saveChat(userChatHistory);
    }

    public UserChatHistory saveChat(UserChatHistory entity) {
        String uuid = entity.getUuid();
        UserChatHistory existingChatHistory = toastTalkChatRepository.findById(uuid)
                .orElse(new UserChatHistory(uuid, entity.getUsername(), entity.getLanguage(), new ArrayList<>()));
        List<History> history = existingChatHistory.getHistory();
        history.addAll(entity.getHistory());
        existingChatHistory.setHistory(history);
        return toastTalkChatRepository.save(existingChatHistory);
    }


    public Object searchHistory(String uuid) {
        JsonNode responseNode = null;
        Map<String, Object> finalResult = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            responseNode = new ObjectMapper().convertValue(elasticSearchRepository.elasticSearchApplications(uuid), JsonNode.class);
            JsonNode output = responseNode.get("hits").get("hits");
            //Throw exception for no returned result
//            if (output.size() == 0) {
//                throw new CustomException("NO_DATA", "No logs data for the given user with the provided search criteria");
//            }



            List<String> userIds = new ArrayList<>();
            if (!isNull(output) && output.isArray()) {
                for (JsonNode objectnode : output) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("History", objectnode.get("_source"));
                    result.add(data);
                }
            }
        } catch (HttpClientErrorException e) {
            log.error("client error while searching ES : " + e.getMessage());
            throw new CustomException("ELASTICSEARCH_ERROR", "client error while searching ES : \" + e.getMessage()");
        }
        return result;
    }
}
