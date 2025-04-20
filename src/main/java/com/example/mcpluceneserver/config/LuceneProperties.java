package com.example.mcpluceneserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "lucene.index")
public class LuceneProperties {
    /**
     * The path where the Lucene index files will be stored.
     */
    private String path = "./lucene-index"; // Default value

    public LuceneProperties() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}