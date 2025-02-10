package com.leonobis.swipesight.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * This class represents the location of a specific user.
 */
public class Location {

    public Location() {
    }

    public Location(Long eventTime, String eventId, Float lat, String name, Float lon, Integer userId) {
        this.eventTime = eventTime;
        this.eventId = eventId;
        this.lat = lat;
        this.name = name;
        this.lon = lon;
        this.userId = userId;
    }

    @JsonProperty("eventTime")
    private Long eventTime;

    @JsonProperty("eventId")
    private String eventId;

    @JsonProperty("lat")
    private Float lat;

    @JsonProperty("name")
    private String name;

    @JsonProperty("lon")
    private Float lon;

    @JsonProperty("userId")
    private Integer userId;

    public Float getLat() {
        return lat;
    }

    public String getName() {
        return name;
    }

    public Float getLon() {
        return lon;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLon(Float lon) {
        this.lon = lon;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Long getEventTime() {
        return eventTime;
    }

    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location that = (Location) o;
        return Objects.equals(lat, that.lat) && Objects.equals(name, that.name) && Objects.equals(lon, that.lon) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, name, lon, userId);
    }

    @Override
    public String toString() {
        return "Location{" +
                "eventTime=" + eventTime +
                ", eventId='" + eventId + '\'' +
                ", lat=" + lat +
                ", name='" + name + '\'' +
                ", lon=" + lon +
                ", userId=" + userId +
                '}';
    }
}
