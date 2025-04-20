package com.example.mcpluceneserver.model;

import java.util.List;
import java.util.Objects;

public class McpQueryRequest {
    private List<McpQuery> queries;

    public McpQueryRequest() {
    }

    public McpQueryRequest(List<McpQuery> queries) {
        this.queries = queries;
    }

    public List<McpQuery> getQueries() {
        return queries;
    }

    public void setQueries(List<McpQuery> queries) {
        this.queries = queries;
    }

    @Override
    public String toString() {
        return "McpQueryRequest{" +
                "queries=" + queries +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        McpQueryRequest that = (McpQueryRequest) o;
        return Objects.equals(queries, that.queries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(queries);
    }
}