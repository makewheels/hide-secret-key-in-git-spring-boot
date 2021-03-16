package com.github.makewheels;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.PropertiesConfigurationLayout;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@SpringBootApplication
public class HideSecretKeyInGitSpringBootApplication {

    public static void main(String[] args) {
        handleKeys();
        SpringApplication.run(HideSecretKeyInGitSpringBootApplication.class, args);
    }

    private static void handleKeys() {
        File propertiesFile = new File(HideSecretKeyInGitSpringBootApplication.class
                .getResource("/application.properties").getPath());
        PropertiesConfiguration config = new PropertiesConfiguration();
        PropertiesConfigurationLayout layout = config.getLayout();
        try {
            layout.load(config, new FileReader(propertiesFile));
            List<String> keys = IteratorUtils.toList(config.getKeys());
            keys.stream().filter(key -> {
                if (StringUtils.isEmpty(key))
                    return false;
                String value = config.get(String.class, key);
                return StringUtils.isNotEmpty(value);
            }).forEach(key -> {
                String value = config.get(String.class, key);
                if (value.startsWith("$CIPHER$")) {
                    config.setProperty(key, System.currentTimeMillis());
                }
            });
            layout.save(config, new FileWriter(propertiesFile, false));
        } catch (ConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

}