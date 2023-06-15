package net.hydrogen2oxygen.arsenic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class Environment {

    private static final Logger logger = LogManager.getLogger(Environment.class);

    private String name;
    private Map<String, String> data = new HashMap<>();

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
     * Add additional data from another environment object
     *
     * @param env to use
     */
    public void addEnvironment(Environment env) {
        data.putAll(env.getData());
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
}
