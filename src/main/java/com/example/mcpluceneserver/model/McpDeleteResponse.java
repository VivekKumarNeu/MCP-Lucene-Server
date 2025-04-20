package com.example.mcpluceneserver.model;

import java.util.Objects;

public class McpDeleteResponse {
    private boolean success; // Simple success indicator
    private int deleted_count; // Number of documents potentially deleted
    // Could also return list of IDs actually deleted

    public McpDeleteResponse() {
    }

    public McpDeleteResponse(boolean success, int deleted_count) {
        this.success = success;
        this.deleted_count = deleted_count;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getDeleted_count() {
        return deleted_count;
    }

    public void setDeleted_count(int deleted_count) {
        this.deleted_count = deleted_count;
    }

    @Override
    public String toString() {
        return "McpDeleteResponse{" +
                "success=" + success +
                ", deleted_count=" + deleted_count +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        McpDeleteResponse that = (McpDeleteResponse) o;
        return success == that.success && deleted_count == that.deleted_count;
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, deleted_count);
    }
}