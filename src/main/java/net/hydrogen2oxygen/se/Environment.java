package net.hydrogen2oxygen.se;

import java.util.HashMap;
import java.util.Map;

public class Environment {

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
        return Integer.parseInt(get(key));
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
