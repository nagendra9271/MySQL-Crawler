package com.mysqlcrawler.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

public class ConfigLoader {

    public static Config loadConfig() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = ConfigLoader.class.getClassLoader().getResourceAsStream("config.json");

        if (is == null) {
            throw new RuntimeException("config.json not found in resources.");
        }

        return mapper.readValue(is, Config.class);
    }
}
