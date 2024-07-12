package io.github.c2hy.clockworks.infrastructure.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Jsons {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T fromJson(byte[] json, Class<T> tClass) {
        try {
            return objectMapper.readValue(json, tClass);
        } catch (IOException e) {
            throw new JsonParseException(e);
        }
    }

    public static String toJson(Object result) {
        try {
            return objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new JsonParseException(e);
        }
    }
}
