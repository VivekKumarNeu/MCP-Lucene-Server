package com.example.mcpluceneserver.model;

import java.util.Map;
import java.util.Objects;

public class McpDocument {
    private String id; // Required: Unique identifier for the document
    private String text; // Required: The main content of the document
    private Map<String, String> metadata; // Optional: Key-value metadata

    public McpDocument() {
    }

    public McpDocument(String id, String text, Map<String, String> metadata) {
        this.id = id;
        this.text = text;
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "McpDocument{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", metadata=" + metadata +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        McpDocument that = (McpDocument) o;
        return Objects.equals(id, that.id) && Objects.equals(text, that.text) && Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, metadata);
    }
}