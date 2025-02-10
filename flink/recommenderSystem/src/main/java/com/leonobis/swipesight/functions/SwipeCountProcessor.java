package com.leonobis.swipesight.functions;

import com.leonobis.swipesight.models.Swipe;
import com.leonobis.swipesight.models.metrics.LongValueMetric;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class SwipeCountProcessor implements AllWindowFunction<Swipe, LongValueMetric, TimeWindow> {
    @Override
    public void apply(
            TimeWindow window,
            Iterable<Swipe> values,
            Collector<LongValueMetric> out
    ) throws Exception {
        long count = 0;
        for (Swipe event : values) {
            count++;
        }
        out.collect(new LongValueMetric(window.getEnd(), count));
    }
}
