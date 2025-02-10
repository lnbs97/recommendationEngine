package com.leonobis.swipesight.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * This class represents a recommendation for a specific user.
 */
public class RecommendationItem {

    public enum RecommendationType {
        CONTENT_BASED_FILTERING,
        COLLABORATIVE_FILTERING,
        TRENDING_PLACE
    }

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("recommendationData")
    private Map<String, Object> recommendationData;

    @JsonProperty("attractionData")
    private Attraction attractionData;

    public RecommendationItem() {
    }

    public RecommendationItem(Map<String, Object> recommendationData, Attraction attractionData) {
        this.recommendationData = recommendationData;
        this.attractionData = attractionData;
    }

    public RecommendationItem(Long userId, Map<String, Object> recommendationData, Attraction attractionData) {
        this.userId = userId;
        this.recommendationData = recommendationData;
        this.attractionData = attractionData;
    }

    public Map<String, Object> getRecommendationData() {
        return recommendationData;
    }

    public Attraction getAttractionData() {
        return attractionData;
    }

    @Override
    public String toString() {
        return "RecommendationItem{" +
                "recommendationData=" + recommendationData +
                ", attractionData=" + attractionData +
                '}';
    }

    public void setRecommendationData(Map<String, Object> recommendationData) {
        this.recommendationData = recommendationData;
    }

    public void setAttractionData(Attraction attractionData) {
        this.attractionData = attractionData;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
