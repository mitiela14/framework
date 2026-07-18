package com.model;

import java.util.Objects;

public class UrlMethod {
    private String url;
    private String method;

    public UrlMethod(String url, String method) {
        this.url = url;
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UrlMethod autre = (UrlMethod) o;
        return Objects.equals(url, autre.url) && Objects.equals(method, autre.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, method);
    }

    @Override 
    public String toString() {
        return "[" + method +  "]" + url ;
    }
}