package com.d121toastmaster.bot.ToastTalker.repository;

import com.d121toastmaster.bot.ToastTalker.Exception.CustomException;
import com.d121toastmaster.bot.ToastTalker.configuration.ESConfiguration;
import com.d121toastmaster.bot.ToastTalker.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Component
@Slf4j
public class ElasticSearchQueryBuilder {
    private ObjectMapper mapper;

    private ESConfiguration config;

    public ElasticSearchQueryBuilder(ObjectMapper mapper, ESConfiguration config) {
        this.mapper = mapper;
        this.config = config;
    }


    private static final String BASE_QUERY = "{\n" +
            "  \"query\": {\n" +
            "  }\n" +
            "}";

    private static final String CREATE_USER_BASE_QUERY = "{\n" +
        "\"timestamp\": {{timestamp}},\n" +
            "  \"userName\": \"{{userName}}\",\n" +
            "  \"uuid\": \"{{userUuid}}\",\n" +
            "  \"jugalUuid\": \"{{jugal-uuid}}\",\n" +
            "  \"request\": \"{{request}}\" \n" +
            "}" ;

    private static final String wildCardQueryTemplate = "{\n" +
            "          \"query_string\": {\n" +
            "            \"default_field\": \"{{VAR}}\",\n" +
            "            \"query\": \"*{{PARAM}}*\"\n" +
            "          }\n" +
            "        }";
    private static final String mustClause = "{\n" +
            "            \"terms\": { \"{{VAR}}\" : \n" +
            "            [ \"{{PARAM}}\"]\n" +
            "           }\n" +
            "        }";

    private static final String filterTemplate = "\"filter\": { " +
            " \"bool\":{} \n" +
            "      }";

    private static final String existsClause = "{\n" +
            "            \"field\":  \"{{VAR}}\" \n" +
            "        }";


    /**
     * Builds a elasticsearch create user document query based on the search criteria
     *
     * @return
     */
    public String getCreateQuery(User user) {

        String finalQuery;
        try {
            String baseQuery = CREATE_USER_BASE_QUERY;
            Long time = System.currentTimeMillis();
            baseQuery = baseQuery.replace("{{timestamp}}", time.toString());
            baseQuery = baseQuery.replace("{{userName}}", user.getUsername().toString());
            baseQuery = baseQuery.replace("{{userUuid}}", user.getUuid().toString());
            baseQuery = baseQuery.replace("{{jugal-uuid}}", "");
            baseQuery = baseQuery.replace("{{request}}", "");

            JsonNode node = mapper.readTree(baseQuery);

            finalQuery = mapper.writeValueAsString(node);

        } catch (Exception e) {
            log.error("ES_ERROR", e);
            throw new CustomException("JSONNODE_ERROR", "Failed to build json query for user creation");
        }

        return finalQuery;

    }

    /**
     * Builds a elasticsearch search query based on the search criteria
     *
     * @return
     */
    public String getSearchQuery(String uuid) {

        String finalQuery;

        try {
            String baseQuery = BASE_QUERY;
                    //addPagination(criteria);
            JsonNode node = mapper.readTree(baseQuery);
            ObjectNode insideMatch = (ObjectNode) node.get("query");
            List<JsonNode> clauses = new LinkedList<>();

            //Adding "must" terms for the search parameters in criteria

            if (!StringUtils.isEmpty("uuid")) {
                clauses.add(getInnerNode(uuid, "uuid.keyword"));
            }

            JsonNode mustNode = mapper.convertValue(new HashMap<String, List<JsonNode>>() {{
                put("must", clauses);
            }}, JsonNode.class);

            insideMatch.put("bool", mustNode);
            ObjectNode boolNode = (ObjectNode) insideMatch.get("bool");

            finalQuery = mapper.writeValueAsString(node);

        } catch (Exception e) {
            log.error("ES_ERROR", e);
            throw new CustomException("JSONNODE_ERROR", "Failed to build json query for fuzzy search");
        }

        return finalQuery;

    }

    /**
     * Creates inner query using the query template
     *
     * @param param
     * @param var
     * @return
     * @throws JsonProcessingException
     */
    private JsonNode getInnerNode(String param, String var) throws JsonProcessingException {

        String template;
        template = mustClause;

        String innerQuery = new String();
        if (param.contains(",")) {
            String[] splitted = param.split(",");
            StringBuilder stringArray = new StringBuilder();
            for (int i = 0; i < splitted.length; i++) {
                if (i < splitted.length - 1) {
                    if (i == 0)
                        stringArray.append("" + splitted[i].trim() + "\",");
                    else
                        stringArray.append("\"" + splitted[i].trim() + "\",");
                } else {
                    stringArray.append("\"" + splitted[i].trim() + "");
                }
            }

            param = stringArray.toString();
            innerQuery = template.replace("{{PARAM}}", param);
        } else
            innerQuery = template.replace("{{PARAM}}", param);
        innerQuery = innerQuery.replace("{{VAR}}", var);


        JsonNode innerNode = mapper.readTree(innerQuery);
        return innerNode;
    }

    /**
     * Creates inner query using the query template for mobileNumber search in WnS module
     *
     * @param param
     * @param var
     * @param var2
     * @return
     * @throws JsonProcessingException
     */
    /*private JsonNode getInnerNodeForMobileNumber(String param, String var, String var2) throws JsonProcessingException {
        List<JsonNode> mobileClause = new LinkedList<>();

        String innerQuery = new String();
        String innerQuery2 = new String();
        innerQuery = template.replace("{{MUST_CLAUSE}}", "must");
        innerQuery = innerQuery.replace("{{PARAM}}", param);
        innerQuery = innerQuery.replace("{{VAR}}", var);

        innerQuery2 = template2.replace("{{MUSTNOT_CLAUSE}}", "must_not");
        innerQuery2 = innerQuery2.replace("{{MUST_CLAUSE}}", "must");
        innerQuery2 = innerQuery2.replace("{{PARAM}}", param);
        innerQuery2 = innerQuery2.replace("{{VAR}}", var);
        innerQuery2 = innerQuery2.replace("{{VAR2}}", var2);

        JsonNode innerNode = mapper.readTree(innerQuery);
        mobileClause.add(innerNode);
        mobileClause.add(mapper.readTree(innerQuery2));
        JsonNode mobileClauseNode = mapper.convertValue(new HashMap<String, List<JsonNode>>() {{
            put("should", mobileClause);
        }}, JsonNode.class);

        return mobileClauseNode;
    }
*/
    /**
     * Adds pagination
     *
     * @param criteria
     * @return baseQuery with pagination
     */
/*
    private String addPagination(InboxSearchCriteria criteria) {
        Long limit = config.getDefaultLimit();
        Long offset = config.getDefaultOffset();

        if (criteria.getLimit() != null && criteria.getLimit() <= config.getMaxSearchLimit())
            limit = Long.valueOf(criteria.getLimit());

        if (criteria.getLimit() != null && criteria.getLimit() > config.getMaxSearchLimit())
            limit = Long.valueOf(config.getMaxSearchLimit());

        if (criteria.getOffset() != null)
            offset = Long.valueOf(criteria.getOffset());

        String baseQuery = BASE_QUERY.replace("{{OFFSET}}", offset.toString());
        baseQuery = baseQuery.replace("{{LIMIT}}", limit.toString());

        return baseQuery;
    }
*/

    /**
     * Escapes special characters in given string
     *
     * @param inputString
     * @return
     */
    private String getEscapedString(String inputString) {
        final String[] metaCharacters = {"\\", "/", "^", "$", "{", "}", "[", "]", "(", ")", ".", "*", "+", "?", "|", "<", ">", "-", "&", "%"};
        for (int i = 0; i < metaCharacters.length; i++) {
            if (inputString.contains(metaCharacters[i])) {
                inputString = inputString.replace(metaCharacters[i], "\\\\" + metaCharacters[i]);
            }
        }
        return inputString;
    }

    /**
     * Merges 2 JSONNodes
     *
     * @param mainNode
     * @param updateNode
     * @return JsonNode
     */
    public static JsonNode merge(JsonNode mainNode, JsonNode updateNode) {

        Iterator<String> fieldNames = updateNode.fieldNames();

        while (fieldNames.hasNext()) {
            String updatedFieldName = fieldNames.next();
            JsonNode valueToBeUpdated = mainNode.get(updatedFieldName);
            JsonNode updatedValue = updateNode.get(updatedFieldName);

            // If the node is an @ArrayNode
            if (valueToBeUpdated != null && valueToBeUpdated.isArray() &&
                    updatedValue.isArray()) {
                // running a loop for all elements of the updated ArrayNode
                for (int i = 0; i < updatedValue.size(); i++) {
                    JsonNode updatedChildNode = updatedValue.get(i);
                    // Create a new Node in the node that should be updated, if there was no corresponding node in it
                    // Use-case - where the updateNode will have a new element in its Array
                    if (valueToBeUpdated.size() <= i) {
                        ((ArrayNode) valueToBeUpdated).add(updatedChildNode);
                    }
                    // getting reference for the node to be updated
                    JsonNode childNodeToBeUpdated = valueToBeUpdated.get(i);
                    merge(childNodeToBeUpdated, updatedChildNode);
                }
                // if the Node is an @ObjectNode
            } else if (valueToBeUpdated != null && valueToBeUpdated.isObject()) {
                merge(valueToBeUpdated, updatedValue);
            } else {
                if (mainNode instanceof ObjectNode) {
                    ((ObjectNode) mainNode).replace(updatedFieldName, updatedValue);
                }
            }
        }
        return mainNode;
    }


}
