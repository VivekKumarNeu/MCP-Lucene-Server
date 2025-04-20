package com.example.mcpluceneserver.model;

import java.util.List;
import java.util.Objects;

public class McpDeleteRequest {
    private List<String> ids;
    // Or: private Map<String, String> filter; // For filter-based deletion

    public McpDeleteRequest() {
    }

    public McpDeleteRequest(List<String> ids) {
        this.ids = ids;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    @Override
    public String toString() {
        return "McpDeleteRequest{" +
                "ids=" + ids +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        McpDeleteRequest that = (McpDeleteRequest) o;
        return Objects.equals(ids, that.ids);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ids);
    }
}