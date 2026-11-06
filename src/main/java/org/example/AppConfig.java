package org.example;

import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = AppConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (in == null) {
                System.err.println("[AppConfig] application.properties не найден в classpath");
            } else {
                PROPS.load(in);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось загрузить application.properties", e);
        }
    }

    public static String get(String key) {
        // 1. Переменная окружения (openrouter.api-key -> OPENROUTER_API_KEY)
        String envKey = key.toUpperCase().replace('.', '_').replace('-', '_');
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) return envValue;

        // 2. Системное свойство (-Dopenrouter.api-key=...)
        String sysValue = System.getProperty(key);
        if (sysValue != null && !sysValue.isBlank()) return sysValue;

        // 3. Значение из файла
        return PROPS.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        String value = get(key);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }

    /** Возвращает значение или падает с понятной ошибкой, если ключа нет. */
    public static String require(String key) {
        String value = get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Не задан обязательный параметр: " + key
                    + " (ни в ENV, ни в application.properties)");
        }
        return value;
    }
}