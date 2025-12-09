package com.camden.skriptutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;
import com.fasterxml.jackson.databind.ObjectMapper;

import static java.text.MessageFormat.format;

public class Utils {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String serialize(Map<String, Object> map, boolean pretty) {
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
            // Pretty...
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

    public static String serialize(Map<String, Object> map) {
        return serialize(map, true);
    }

    public static Map<String, Object> deserialize(String jsonString) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = mapper.readValue(jsonString, Map.class);
            return map;
        } catch (Exception ex) {
            LoggerUtil.getLogger().warning(
                format("Dict deserialization failed: '{0}'", ex.getMessage())
            );
        }
        return null;
    }

    public static List<String> getKeys(Map<String, Object> map) {
        return new ArrayList<>(map.keySet());
    }

    public static long getEpochTime() {
        return Instant.now().getEpochSecond();
    }

    public static List<String> createSplitCommasList(String string) {
        return Arrays.asList(string.split(", "));
    }

    public static List<String> createEmptyList() {
        return new ArrayList<>();
    }
}
