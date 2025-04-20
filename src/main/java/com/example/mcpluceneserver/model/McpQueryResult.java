package com.example.mcpluceneserver.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class McpQueryResult {
    private List<Result> results;

    public McpQueryResult() {
    }

    public McpQueryResult(List<Result> results) {
        this.results = results;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "McpQueryResult{" +
                "results=" + results +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        McpQueryResult that = (McpQueryResult) o;
        return Objects.equals(results, that.results);
    }

    @Override
    public int hashCode() {
        return Objects.hash(results);
    }

    public static class Result {
        private String id;
        private String text; // Often omitted in query results unless specifically requested/stored
        private Map<String, String> metadata;
        private float score;

        public Result() {
        }

        public Result(String id, String text, Map<String, String> metadata, float score) {
            this.id = id;
            this.text = text;
            this.metadata = metadata;
            this.score = score;
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

        public float getScore() {
            return score;
        }

        public void setScore(float score) {
            this.score = score;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "id='" + id + '\'' +
                    ", text='" + text + '\'' +
                    ", metadata=" + metadata +
                    ", score=" + score +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Result result = (Result) o;
            return Float.compare(result.score, score) == 0 && Objects.equals(id, result.id) && Objects.equals(text, result.text) && Objects.equals(metadata, result.metadata);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, text, metadata, score);
        }
    }
}