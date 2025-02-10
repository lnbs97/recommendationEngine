package com.leonobis.swipesight.models.metrics;

import java.util.List;

public class PopularAttractions {
    public Long eventTime;
    public Long windowStart;
    public Long windowEnd;
    public List<AttractionWithLikeCount> popularAttractions;

    public PopularAttractions(Long eventTime, Long windowStart, Long windowEnd, List<AttractionWithLikeCount> attractionWithLikeCountList) {
        this.eventTime = eventTime;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        this.popularAttractions = attractionWithLikeCountList;
    }

    public PopularAttractions() {
    }

    @Override
    public String toString() {
        return "TopAttractions{" +
                "eventTime=" + eventTime +
                ", windowStart=" + windowStart +
                ", windowEnd=" + windowEnd +
                ", popularAttractions=" + popularAttractions +
                '}';
    }
}
