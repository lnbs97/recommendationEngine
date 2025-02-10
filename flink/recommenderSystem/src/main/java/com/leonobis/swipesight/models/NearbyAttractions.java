package com.leonobis.swipesight.models;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class is used to manage attractions which are close to a user.
 */
public class NearbyAttractions {
    private int userId;
    private List<Attraction> nearbyAttractions;

    public NearbyAttractions() {
    }

    public NearbyAttractions(int userId, List<Attraction> nearbyAttractions) {
        this.userId = userId;
        this.nearbyAttractions = nearbyAttractions;
    }

    public List<Long> getAttractionIds() {
        return nearbyAttractions.stream().map(Attraction::getId).collect(Collectors.toList());
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<Attraction> getNearbyAttractions() {
        return nearbyAttractions;
    }

    public void setNearbyAttractions(List<Attraction> nearbyAttractions) {
        this.nearbyAttractions = nearbyAttractions;
    }

    @Override
    public String toString() {
        return "NearbyAttractions{" +
                "userId=" + userId +
                ", nearbyAttractions=" + nearbyAttractions +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NearbyAttractions that = (NearbyAttractions) o;
        return userId == that.userId && Objects.equals(nearbyAttractions, that.nearbyAttractions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, nearbyAttractions);
    }
}
