package com.example.mcpluceneserver.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

    @SpringBootApplication(scanBasePackages = "com.example.mcpluceneserver")
    public class McpLuceneServerApplication {

        public static void main(String[] args) {
            SpringApplication.run(McpLuceneServerApplication.class, args);
        }
    }
