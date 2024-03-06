package com.d121toastmaster.bot.ToastTalker.repository;

import com.d121toastmaster.bot.ToastTalker.Exception.CustomException;
import com.d121toastmaster.bot.ToastTalker.configuration.ESConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
@Slf4j
public class ElasticSearchRepository {

    private ESConfiguration config;

    ElasticSearchQueryBuilder queryBuilder;

    private RestTemplate restTemplate;

    private ObjectMapper mapper;

    private Object response;


    @Autowired
    public ElasticSearchRepository(ESConfiguration config, ElasticSearchQueryBuilder queryBuilder, RestTemplate restTemplate, ObjectMapper mapper) {
        this.config = config;
        this.queryBuilder = queryBuilder;
        this.restTemplate = restTemplate;
        this.mapper = mapper;
    }


    /**
     * Searches records from elasticsearch based on the fuzzy search criteria
     *
     * @return
     */
    public Object elasticSearchApplications(String uuid) {

        String url = getESURL();

        String searchQuery = queryBuilder.getSearchQuery(uuid);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(searchQuery, headers);
        ResponseEntity response = null;
        try {
            response = restTemplate.postForEntity(url, requestEntity, Object.class);

        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("ES_SEARCH_ERROR", "Failed to fetch data from ES");
        }

        return response.getBody();

    }


    /**
     * Generates elasticsearch search url from application properties
     *
     * @return
     */
    private String getESURL() {

        StringBuilder builder = new StringBuilder(config.getIndexServiceHost());
        builder.append(config.getEsUserChatHistoryIndex());
        builder.append(config.getIndexServiceHostSearchEndpoint());
        return builder.toString();
    }

}
