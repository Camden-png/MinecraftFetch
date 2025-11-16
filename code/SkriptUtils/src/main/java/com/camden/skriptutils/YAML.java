package com.camden.skriptutils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.text.MessageFormat.format;

public class YAML {
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    public static Dict load(String yamlName) {
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
                return new Dict();
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> yamlData = mapper.readValue(yamlFile, LinkedHashMap.class);

            Dict dict = new Dict();
            dict.putAll(yamlData);
            return dict;
        } catch (Exception ex) {
            LoggerUtil.getLogger().warning(
                format("Error: '{0}'", ex.getMessage())
            );
        }
        return new Dict();
    }
}
