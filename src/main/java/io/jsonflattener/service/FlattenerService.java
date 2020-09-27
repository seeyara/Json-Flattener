package io.jsonflattener.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class FlattenerService {
    private ObjectMapper mapper;

    public FlattenerService(ObjectMapper objectMapper) {
        this.mapper = objectMapper;
    }

    private JsonNode flattenJson(JsonNode input, ObjectNode response, String jsonKey) {
        input.fieldNames().forEachRemaining(key -> {
            String field = "".equals(jsonKey) ? key : jsonKey + "." + key;
            if (input.get(key).isArray() && input.get(key).size() >= 1) {
                for (int i = 0; i < input.get(key).size(); i++) {
                    flattenJson(input.get(key).get(i), response, field + "[" + i + "]");
                }
            } else if (input.get(key).isObject()) {
                flattenJson(input.get(key), response, field);
            } else {
                response.set(field, input.get(key));
            }
        });
        return response;
    }

    public JsonNode flattenJson(String jsonStr) throws Exception {
        JsonNode response = null;
        try {
            JsonNode input = mapper.readValue(jsonStr, JsonNode.class);
            response = flattenJson(input, mapper.createObjectNode(), "");
        } catch (JsonProcessingException e) {
            throw new Exception("Not a valid json");
        }
        return response;
    }
}
