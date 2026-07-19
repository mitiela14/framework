package com.model;

import java.util.HashMap;
import java.util.Map;


public class ModelView {

    private String url;
    private Map<String, Object> data = new HashMap<>();  // les donnees a donner a la vue

    public ModelView() {}

    public ModelView(String url) {
        this.url = url;
    }

    public void addAttribute(String key, Object value) {
        data.put(key, value);
    }

    public void setUrl(String url) { this.url = url; }
    public String getUrl()         { return url; }

    public Map<String, Object> getData() { return data; }
}
