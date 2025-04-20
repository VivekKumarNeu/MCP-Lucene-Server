package com.example.mcpluceneserver.model;

import java.util.Map;
import java.util.Objects;

public class McpQuery {
    private String query; // Required: The search query string
    private Integer top_k = 10; // Optional: Number of results to return (default 10)
    private Map<String, String> filter; // Optional: Filter based on metadata fields

    public McpQuery() {
    }

    public McpQuery(String query, int topK, Map<String, String> filter) {
        this.query = query;
        this.top_k = topK;
        this.filter = filter;

    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getTop_k() {
        return top_k;
    }

    public void setTop_k(Integer top_k) {
        this.top_k = top_k;
    }

    public Map<String, String> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, String> filter) {
        this.filter = filter;
    }

    @Override
    public String toString() {
        return "McpQuery{" +
                "query='" + query + '\'' +
                ", top_k=" + top_k +
                ", filter=" + filter +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        McpQuery mcpQuery = (McpQuery) o;
        return Objects.equals(query, mcpQuery.query) && Objects.equals(top_k, mcpQuery.top_k) && Objects.equals(filter, mcpQuery.filter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, top_k, filter);
    }
}