package com.camden.skriptutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import static java.text.MessageFormat.format;

public class Dict {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, Object> map = new LinkedHashMap<>();

    public void put(String key, Object value) {
        map.put(key, value);
    }

    public Object get(String key) {
        return map.get(key);
    }

    public List<String> getKeys() {
        return new ArrayList<>(map.keySet());
    }

    public String getKeysAsString() {
        return String.join(",", getKeys());
    }

    public String serialize() {
        try {
            return mapper.writeValueAsString(map);
        } catch (Exception ex) {
            System.err.println(
                format("Dict serialization failed: '{0}'", ex.getMessage())
            );
        }
        return null;
    }

    public static Dict deserialize(String jsonString) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = mapper.readValue(jsonString, Map.class);
            Dict dict = new Dict();
            dict.map.putAll(data);
            return dict;
        } catch (Exception ex) {
            System.err.println(
                format("Dict deserialization failed: '{0}'", ex.getMessage())
            );
        }
        return null;
    }
}
