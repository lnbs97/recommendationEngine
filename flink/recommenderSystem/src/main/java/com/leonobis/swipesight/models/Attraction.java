package com.leonobis.swipesight.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

/**
 * This class represents an attraction.
 */
public class Attraction {

    @JsonProperty("osm_id")
    private Long osm_id;

    @JsonProperty("dist_meters")
    private Float dist_meters;

    @JsonProperty("name")
    private String name;

    @JsonProperty("lon")
    private Float lon;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("lat")
    private Float lat;

    @JsonProperty("tags")
    private HashMap<String, String> tags;

    public Attraction() {
    }

    public Attraction(Long osm_id, Float dist_meters, String name, Float lon, Long id, Float lat, Map<String, String> tags) {
        this.osm_id = osm_id;
        this.dist_meters = dist_meters;
        this.name = name;
        this.lon = lon;
        this.id = id;
        this.lat = lat;
        this.tags = new HashMap<>(tags);
    }

    public Long getOsm_id() {
        return osm_id;
    }

    public Float getDist_meters() {
        return dist_meters;
    }

    public String getName() {
        return name;
    }

    public Float getLon() {
        return lon;
    }

    public Long getId() {
        return id;
    }

    public Float getLat() {
        return lat;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public List<String> getTagsAsStrings() {
        ArrayList<String> stringList = new ArrayList<>();
        for (Map.Entry<String, String> tagEntry : tags.entrySet()) {
            String tag = tagEntry.getKey() + ":" + tagEntry.getValue();
            stringList.add(tag);
        }
        return stringList;
    }

    public void setOsm_id(Long osm_id) {
        this.osm_id = osm_id;
    }

    public void setDist_meters(Float dist_meters) {
        this.dist_meters = dist_meters;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLon(Float lon) {
        this.lon = lon;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = new HashMap<>(tags);
    }

    @Override
    public String toString() {
        return "AttractionData{" +
                "osm_id='" + osm_id + '\'' +
                ", dist_meters=" + dist_meters +
                ", name='" + name + '\'' +
                ", lon=" + lon +
                ", id=" + id +
                ", lat=" + lat +
                ", tags=" + tags +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attraction that = (Attraction) o;
        return Objects.equals(osm_id, that.osm_id) && Objects.equals(dist_meters, that.dist_meters) && Objects.equals(name, that.name) && Objects.equals(lon, that.lon) && Objects.equals(id, that.id) && Objects.equals(lat, that.lat) && Objects.equals(tags, that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(osm_id, dist_meters, name, lon, id, lat, tags);
    }
}