package com.example.mcpluceneserver.model;

import java.util.Map;
import java.util.Objects;

public class McpStatusResponse {
    private String status;
    private String index_location;
    private long num_docs; // Approximate number of documents
    private Map<String, Object> details; // Other relevant System.out.println

    public McpStatusResponse() {
    }

    public McpStatusResponse(String status, String index_location, long num_docs, Map<String, Object> details) {
        this.status = status;
        this.index_location = index_location;
        this.num_docs = num_docs;
        this.details = details;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIndex_location() {
        return index_location;
    }

    public void setIndex_location(String index_location) {
        this.index_location = index_location;
    }

    public long getNum_docs() {
        return num_docs;
    }

    public void setNum_docs(long num_docs) {
        this.num_docs = num_docs;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "McpStatusResponse{" +
                "status='" + status + '\'' +
                ", index_location='" + index_location + '\'' +
                ", num_docs=" + num_docs +
                ", details=" + details +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        McpStatusResponse that = (McpStatusResponse) o;
        return num_docs == that.num_docs && Objects.equals(status, that.status) && Objects.equals(index_location, that.index_location) && Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, index_location, num_docs, details);
    }
}