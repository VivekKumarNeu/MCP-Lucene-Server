package com.example.mcpluceneserver.model;

import java.util.List;
import java.util.Objects;

public class McpQueryResponse {
    private List<McpQueryResult> results; // List of results, one per input query

    public McpQueryResponse() {
    }

    public McpQueryResponse(List<McpQueryResult> results) {
        this.results = results;
    }

    public List<McpQueryResult> getResults() {
        return results;
    }

    public void setResults(List<McpQueryResult> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "McpQueryResponse{" +
                "results=" + results +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        McpQueryResponse that = (McpQueryResponse) o;
        return Objects.equals(results, that.results);
    }

    @Override
    public int hashCode() {
        return Objects.hash(results);
    }
}