package exercice;

import java.util.Objects;

public class CleAvecEquals {
    private String url;
    private String method;

    public CleAvecEquals(String url, String method) {
        this.url = url;
        this.method = method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CleAvecEquals autre = (CleAvecEquals) o;
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