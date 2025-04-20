package com.example.mcpluceneserver.model;

import java.util.List;
import java.util.Objects;

public class McpUpsertResponse {
    private List<String> ids; // IDs of successfully upserted documents

    public McpUpsertResponse() {
    }

    public McpUpsertResponse(List<String> ids) {
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
        return "McpUpsertResponse{" +
                "ids=" + ids +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        McpUpsertResponse that = (McpUpsertResponse) o;
        return Objects.equals(ids, that.ids);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ids);
    }
}