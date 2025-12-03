package com.camden.skriptutils;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import static java.text.MessageFormat.format;

public class Dict {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, Object> map = new LinkedHashMap<>();

    public Map<String, Object> getMap() {
        return map;
    }

    public void put(String key, Object value) {
        map.put(key, value);
    }

    public void putAll(Map<String, Object> _map) {
        map.putAll(_map);
    }

    public Object get(String key) {
        return map.get(key);
    }

    // Allow Python-like `get(...)` but also Java's syntax...
    public Object get(String key, Object _default) {
        return map.getOrDefault(key, _default);
    }

    public Object getOrDefault(String key, Object _default) {
        return map.getOrDefault(key, _default);
    }

    public List<String> getKeys() {
        return new ArrayList<>(map.keySet());
    }

    public String serialize(boolean pretty) {
        try {
            if (!pretty) {
                DefaultPrettyPrinter printer = new DefaultPrettyPrinter(
                    Separators.createDefaultInstance()
                        .withObjectFieldValueSpacing(Separators.Spacing.AFTER)
                        .withObjectEntrySpacing(Separators.Spacing.AFTER)
                );
                printer.indentObjectsWith(new DefaultIndenter("", ""));
                printer.indentArraysWith(new DefaultIndenter("", ""));
                return mapper.writer(printer).writeValueAsString(map);
            }
            DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
            printer.indentObjectsWith(new DefaultIndenter("  ", "\n"));
            printer.indentArraysWith(new DefaultIndenter("  ", "\n"));
            return mapper.writer(printer).writeValueAsString(map);
        } catch (Exception ex) {
            LoggerUtil.getLogger().warning(
                format("Dict serialization failed: '{0}'", ex.getMessage())
            );
        }
        return null;
    }

    public String serialize() {
        return serialize(true);
    }

    public static Dict deserialize(String jsonString) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = mapper.readValue(jsonString, Map.class);
            Dict dict = new Dict();
            dict.map.putAll(data);
            return dict;
        } catch (Exception ex) {
            LoggerUtil.getLogger().warning(
                format("Dict deserialization failed: '{0}'", ex.getMessage())
            );
        }
        return null;
    }
}
