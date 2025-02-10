package com.leonobis.swipesight.models.metrics;

public class LongValueMetric {
    public Long eventTime;
    public Long value;

    public LongValueMetric(Long eventTime, Long value) {
        this.eventTime = eventTime;
        this.value = value;
    }
}
