package com.leonobis.swipesight.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leonobis.swipesight.models.Attraction;
import com.leonobis.swipesight.util.Config;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This services uses JDBC to fetch attractions from the database.
 */
public class AttractionService {
    public static List<Attraction> fetchAttractions(Float lon, Float lat) {
        // JDBC connection details
        String hostname = Config.getDatabaseHost();
        String jdbcUrl = "jdbc:postgresql://" + hostname + "/swipesight";
        String username = "admin";
        String password = "example";
        String query = "SELECT * FROM attractions_by_location(?, ?, ?)";

        List<Attraction> attractions = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                // Set parameters for the query
                statement.setFloat(1, lat);  // latitude
                statement.setFloat(2, lon);  // longitude
                statement.setInt(3, 5000);   // radius in meters

                // Execute the query and process the results
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Attraction attraction = createAttractionFromResultSet(resultSet);
                        attractions.add(attraction);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attractions;
    }

    private static Attraction createAttractionFromResultSet(ResultSet rs) throws SQLException {
        Attraction attraction = new Attraction();
        attraction.setId(rs.getLong("id"));
        attraction.setOsm_id(rs.getLong("osm_id"));
        attraction.setName(rs.getString("name"));
        attraction.setLat(rs.getFloat("lat"));
        attraction.setLon(rs.getFloat("lon"));
        attraction.setDist_meters(rs.getFloat("dist_meters"));

        String tagsJson = rs.getString("tags");
        if (tagsJson != null && !tagsJson.isEmpty()) {
            attraction.setTags(parseTagsJson(tagsJson));
        } else {
            attraction.setTags(new HashMap<>());
        }
        return attraction;
    }

    private static Map<String, String> parseTagsJson(String tagsJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(tagsJson, new TypeReference<Map<String, String>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
