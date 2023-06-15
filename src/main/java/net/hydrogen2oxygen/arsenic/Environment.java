package net.hydrogen2oxygen.arsenic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class Environment {

    private static final Logger logger = LogManager.getLogger(Environment.class);

    private String name;
    private Map<String, String> data = new HashMap<>();
    private Map<String, Environment> additionalEnvironments = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public String get(String key) {
        return getData().get(key);
    }
    public String get(String environmentName, String key) {
        Environment environment = additionalEnvironments.get(environmentName);
        if (environment == null) return null;
        return environment.get(key);
    }

    public Integer getInt(String key) {
        try {
            String value = get(key);
            if (value != null) {
                return Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {
            logger.error(e);
        }
        return null;
    }

    /**
     * Add more data from another environment object
     *
     * @param env to use
     */
    public void addEnvironment(Environment env) {
        data.putAll(env.getData());
    }

    /**
     * Add additional environment, that might have the same key for different values
     * @param env
     */
    public void addAdditionalEnvironment(Environment env) {
        additionalEnvironments.put(env.name, env);
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
}
