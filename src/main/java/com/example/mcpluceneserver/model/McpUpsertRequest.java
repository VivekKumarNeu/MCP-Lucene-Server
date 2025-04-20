package com.example.mcpluceneserver.model;

import java.util.List;
import java.util.Objects;

// Generic wrapper if needed, or specific request types
public class McpUpsertRequest {
    private List<McpDocument> documents;

    public McpUpsertRequest() {
    }

    public McpUpsertRequest(List<McpDocument> documents) {
        this.documents = documents;
    }

    public List<McpDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<McpDocument> documents) {
        this.documents = documents;
    }

    @Override
    public String toString() {
        return "McpUpsertRequest{" +
                "documents=" + documents +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        McpUpsertRequest that = (McpUpsertRequest) o;
        return Objects.equals(documents, that.documents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documents);
    }
}