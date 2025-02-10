package com.leonobis.swipesight.util;

public class Config {
    private static final String KAFKA_BOOTSTRAP_SERVERS;
    private static final String DATABASE_HOST;

    static {
        KAFKA_BOOTSTRAP_SERVERS = System.getenv("KAFKA_BOOTSTRAP_SERVERS");
        DATABASE_HOST = System.getenv("DATABASE_HOST");
    }

    public static String getKafkaBootstrapServers() {
        return KAFKA_BOOTSTRAP_SERVERS != null ? KAFKA_BOOTSTRAP_SERVERS : "localhost:9092";
    }

    public static String getDatabaseHost() {
        return DATABASE_HOST != null ? DATABASE_HOST : "localhost:5432";
    }
}

