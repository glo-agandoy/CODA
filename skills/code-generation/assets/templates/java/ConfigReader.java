package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigReader — Singleton loader for config.properties.
 * Ensures all test configuration is externalized, never hardcoded.
 *
 * Usage:
 *   ConfigReader config = ConfigReader.getInstance();
 *   String url = config.getBaseUrl();
 *
 * @author Code Generation Skill
 * @version 2.0
 */
public class ConfigReader {

    private static volatile ConfigReader instance;
    private final Properties props = new Properties();

    private ConfigReader() {
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (is == null) {
                throw new RuntimeException("config.properties not found in classpath. "
                    + "Ensure it exists at src/test/resources/config.properties");
            }
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    /** Thread-safe singleton accessor. */
    public static ConfigReader getInstance() {
        if (instance == null) {
            synchronized (ConfigReader.class) {
                if (instance == null) {
                    instance = new ConfigReader();
                }
            }
        }
        return instance;
    }

    // ==================== GETTERS ====================

    public String getBaseUrl() {
        return getRequired("base.url");
    }

    public String getBrowser() {
        return props.getProperty("browser", "chrome");
    }

    public boolean isHeadless() {
        return Boolean.parseBoolean(props.getProperty("headless", "false"));
    }

    /** Explicit wait timeout in seconds (default: 15) */
    public int getExplicitTimeout() {
        return Integer.parseInt(props.getProperty("timeout.explicit", "15"));
    }

    /** Spinner wait timeout in seconds (default: 30) */
    public int getSpinnerTimeout() {
        return Integer.parseInt(props.getProperty("timeout.spinner", "30"));
    }

    /** Page load timeout in seconds (default: 30) */
    public int getPageLoadTimeout() {
        return Integer.parseInt(props.getProperty("timeout.page.load", "30"));
    }

    public String getTestUserEmail() {
        // First try environment variable, then config file
        String env = System.getenv("TEST_USER_EMAIL");
        return env != null ? env : props.getProperty("test.user.email", "");
    }

    public String getTestUserPassword() {
        String env = System.getenv("TEST_USER_PASSWORD");
        return env != null ? env : props.getProperty("test.user.password", "");
    }

    public boolean shouldCaptureScreenshotOnFailure() {
        return Boolean.parseBoolean(props.getProperty("screenshot.on.failure", "true"));
    }

    // ==================== HELPER ====================

    private String getRequired(String key) {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new RuntimeException("Required property '" + key + "' is missing or empty in config.properties");
        }
        return value;
    }
}
