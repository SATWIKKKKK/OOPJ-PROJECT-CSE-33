package com.college.utils;

import java.io.*;
import java.util.Properties;

/**
 * EnvConfig — reads credentials from a .env file.
 * Falls back to defaults if the file is missing.
 */
public class EnvConfig {

    private static final Properties props = new Properties();
    private static boolean loaded = false;

    static {
        load();
    }

    private static void load() {
        // Try multiple locations for the .env file
        String[] paths = { ".env", "../.env", "../../.env" };
        for (String path : paths) {
            File file = new File(path);
            if (file.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) continue;
                        int idx = line.indexOf('=');
                        if (idx > 0) {
                            String key = line.substring(0, idx).trim();
                            String val = line.substring(idx + 1).trim();
                            props.setProperty(key, val);
                        }
                    }
                    loaded = true;
                    break;
                } catch (IOException e) {
                    System.err.println("  [EnvConfig] Warning: Could not read " + path);
                }
            }
        }
        if (!loaded) {
            System.out.println("  [EnvConfig] .env file not found — using default values.");
        }
    }

    public static String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public static String get(String key) {
        return props.getProperty(key, "");
    }

    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static boolean isLoaded() { return loaded; }
}
