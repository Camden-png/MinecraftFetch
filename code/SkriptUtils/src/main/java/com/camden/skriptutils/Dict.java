package com.camden.skriptutils;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
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

    public void putAll(Map<String, Object> _map) {
        map.putAll(_map);
    }

    public Object get(String key) {
        return map.get(key);
    }

    public List<String> getKeys() {
        return new ArrayList<>(map.keySet());
    }

    public String serialize() {
        try {
            DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
            printer.indentObjectsWith(new DefaultIndenter("  ", "\n"));
            printer.indentArraysWith(new DefaultIndenter("  ", "\n"));
            return mapper.writer(printer).writeValueAsString(map);
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
