package com.leonobis.swipesight.models.metrics;

public class AttractionWithLikeCount {
    public Long attractionId;
    public Long likeCount;

    public AttractionWithLikeCount(Long attractionId, Long likeCount) {
        this.attractionId = attractionId;
        this.likeCount = likeCount;
    }

    @Override
    public String toString() {
        return "AttractionWithSwipeCount{" +
                "attractionId=" + attractionId +
                ", likeCount=" + likeCount +
                '}';
    }
}
