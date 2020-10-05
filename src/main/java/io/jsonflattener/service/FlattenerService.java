package io.jsonflattener.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;

public class FlattenerService {
    private ObjectMapper mapper;

    public FlattenerService(ObjectMapper objectMapper) {
        this.mapper = objectMapper;
    }

    private JsonNode flattenJson(JsonNode input, ObjectNode response, String jsonKey) {
        if (input.isArray()) {
            int i = 0;
            for (Iterator<String> it = input.; it.hasNext(); ) {
                String node = it.next();
                flattenJson(input.get(node), response, node + "[" + i + "]");
            }
        } else {
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
        }
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

    public JsonNode unflattenJson(String jsonStr) throws Exception {
        JsonNode response = null;
        try {
            JsonNode input = mapper.readValue(jsonStr, JsonNode.class);
            response = unflattenJson(input, mapper.createObjectNode());
        } catch (JsonProcessingException e) {
            throw new Exception("Not a valid json");
        }
        return response;
    }

    private JsonNode unflattenJson(JsonNode input, ObjectNode objectNode) {
        int i = 0;
        for (Iterator<String> it = input.fieldNames(); it.hasNext(); ) {
            String key = it.next();
            String[] keyArray = key.split("\\.");
            processJson(keyArray, i, objectNode, input, key);
        }
        return objectNode;
    }

    private void processJson(String[] keyArray, int i, ObjectNode objectNode, JsonNode input, String key) {
        if (!keyArray[i].contains("[")) {
            if (!objectNode.has(keyArray[i])) {
                objectNode.set(keyArray[i], mapper.createObjectNode());
            }
            if (keyArray.length > i + 1)
                processJson(keyArray, i + 1, (ObjectNode) objectNode.get(keyArray[i]), input, key);
            else objectNode.set(keyArray[i], input.get(key));
        } else {
            String arrayKey = keyArray[i].split("\\[")[0];
            if (!objectNode.has(arrayKey)) {
                ArrayNode value = mapper.createArrayNode();
                objectNode.set(arrayKey, value);
            }
            if (keyArray.length > i + 1)
                processJson(keyArray, i + 1, (ArrayNode) objectNode.get(arrayKey), input, key, 0);
            else objectNode.set(arrayKey, input.get(key));
        }
    }

    private void processJson(String[] keyArray, int i, ArrayNode arrayNode, JsonNode input, String key, int j) {
        if (!keyArray[i].contains("[")) {
            if (keyArray.length > i + 1)
                processJson(keyArray, i + 1, (ObjectNode) arrayNode.get(keyArray[i]), input, key);
            else {
                ObjectNode value = mapper.createObjectNode();
                if (arrayNode.size() > 0 && j - 1 <= arrayNode.size()) {
                    value = (ObjectNode) arrayNode.get(j - 1);
                }
                value.set(keyArray[i], input.get(key));
                if (j - 1 < arrayNode.size())
                    arrayNode.set(j - 1, value);
                else arrayNode.add(value);
            }
        } else {
            String arrayKey = keyArray[i].split("\\[")[0];
            ObjectNode currentNode = (ObjectNode) arrayNode.get(j);
            if (arrayNode.size() == 0 || (arrayNode.size() > 0 && !arrayNode.get(j).has(arrayKey))) {
                ObjectNode value = mapper.createObjectNode();
                value.set(arrayKey, mapper.createArrayNode());
                arrayNode.add(value);
                currentNode = (ObjectNode) arrayNode.get(arrayNode.size() - 1);
            }
            if (keyArray.length > i + 1)
                processJson(keyArray, i + 1, (ArrayNode) currentNode.get(arrayKey), input, key, j + 1);
            else arrayNode.add(input.get(key));
        }
    }
}
