package com.leonobis.swipesight.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * This class represents a swipe of a specific user.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Swipe {

	public Swipe() {
	}

	public Swipe(Long eventTime, String eventId, Long attractionId, String swipeType, int userId, Map<String, String> attractionTags) {
		this.eventTime = eventTime;
		this.eventId = eventId;
		this.attractionId = attractionId;
		this.swipeType = swipeType;
		this.userId = userId;
		this.attractionTags = attractionTags;
	}

	@JsonProperty("eventTime")
	private Long eventTime;

	@JsonProperty("eventId")
	private String eventId;

	@JsonProperty("attractionId")
	private Long attractionId;

	@JsonProperty("swipeType")
	private String swipeType;

	@JsonProperty("userId")
	private int userId;

	@JsonProperty("attractionTags")
	private Map<String, String> attractionTags;

	public Long getAttractionId(){
		return attractionId;
	}

	public String getSwipeType(){
		return swipeType;
	}

	public int getUserId(){
		return userId;
	}

	public Map<String, String> getAttractionTags(){
		return attractionTags;
	}

	public void setAttractionId(Long attractionId) {
		this.attractionId = attractionId;
	}

	public void setSwipeType(String swipeType) {
		this.swipeType = swipeType;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setAttractionTags(Map<String, String> attractionTags) {
		this.attractionTags = attractionTags;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public Long getEventTime() {
		return eventTime;
	}

	public void setEventTime(Long eventTime) {
		this.eventTime = eventTime;
	}

	@Override
	public String toString() {
		return "Swipe{" +
				"eventTime=" + eventTime +
				", eventId='" + eventId + '\'' +
				", attractionId=" + attractionId +
				", swipeType='" + swipeType + '\'' +
				", userId=" + userId +
				", attractionTags=" + attractionTags +
				'}';
	}
}