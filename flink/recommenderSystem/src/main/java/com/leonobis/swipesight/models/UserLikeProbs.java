package com.leonobis.swipesight.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class is used to keep track of a users like probabilities.
 */
public class UserLikeProbs {
    private Long eventTime;
    private int userId;
    private Map<String, Double> likeProbs;

    public UserLikeProbs() {
        likeProbs = new HashMap<>();
    }

    public UserLikeProbs(Long eventTime, int userId, Map<String, Double> likeProbs) {
        this.eventTime = eventTime;
        this.userId = userId;
        this.likeProbs = likeProbs;
    }

    @Override
    public String toString() {
        return "UserLikeProbs{" +
                "eventTime=" + eventTime +
                ", userId=" + userId +
                ", likeProbs=" + likeProbs +
                '}';
    }

    public Long getEventTime() {
        return eventTime;
    }

    public void setEventTime(Long eventTime) {
        this.eventTime = eventTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Map<String, Double> getLikeProbs() {
        return likeProbs;
    }

    public void setLikeProbs(Map<String, Double> likeProbs) {
        this.likeProbs = likeProbs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserLikeProbs that = (UserLikeProbs) o;
        return userId == that.userId && Objects.equals(eventTime, that.eventTime) && Objects.equals(likeProbs, that.likeProbs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventTime, userId, likeProbs);
    }
}
