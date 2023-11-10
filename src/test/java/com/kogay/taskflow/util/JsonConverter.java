package com.kogay.taskflow.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonConverter {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJson(String key, Object value) {
        try {
            ObjectNode jsonNode = objectMapper.createObjectNode();
            jsonNode.putPOJO(key, value);
            return toJson(jsonNode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
