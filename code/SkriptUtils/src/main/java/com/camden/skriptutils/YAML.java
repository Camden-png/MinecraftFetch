package com.camden.skriptutils;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import static java.text.MessageFormat.format;

public class YAML {
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    public static Map<String, Object> load(String yamlName) {
        try {
            File jarDir = new File(YAML.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI()).getParentFile();
            File pluginsDir = jarDir.getParentFile();
            File yamlDir = new File(pluginsDir, "levels");
            File yamlFile = new File(yamlDir, yamlName);

            if (!yamlFile.exists()) {
                LoggerUtil.getLogger().warning(
                    format("Error: 'YAML {0} does not exist!'", yamlName)
                );
                return new LinkedHashMap<>();
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> map = mapper.readValue(yamlFile, LinkedHashMap.class);
            return map;
        } catch (Exception ex) {
            LoggerUtil.getLogger().warning(
                format("Error: '{0}'", ex.getMessage())
            );
        }
        return new LinkedHashMap<>();
    }
}
