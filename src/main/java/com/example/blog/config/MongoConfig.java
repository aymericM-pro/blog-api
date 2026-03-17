package com.example.blog.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.List;

@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "com.example.blog.repository")
public class MongoConfig {

    @Bean
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(
                "mongodb://blog_user:blog_pass@localhost:27017/blog_db?authSource=admin"
        );
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(settings);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, "blog_db");
    }

    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(List.of());
    }
}