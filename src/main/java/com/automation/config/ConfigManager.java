package com.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads framework configuration once from {@code config.properties} on the
 * classpath and exposes it as typed accessors. Keeping all environment
 * knobs (base URL, credentials, logging) here means tests never hardcode
 * them, and switching environments is a one-file change.
 */
public final class ConfigManager {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException("Unable to find " + CONFIG_FILE + " on the classpath");
            }
            PROPERTIES.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + CONFIG_FILE, e);
        }
    }

    private ConfigManager() {
    }

    public static String baseUri() {
        return get("base.uri");
    }

    public static String authUsername() {
        return get("auth.username");
    }

    public static String authPassword() {
        return get("auth.password");
    }

    public static boolean logRequests() {
        return Boolean.parseBoolean(PROPERTIES.getProperty("log.requests", "false"));
    }

    private static String get(String key) {
        String value = PROPERTIES.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required config key: " + key);
        }
        return value;
    }
}
