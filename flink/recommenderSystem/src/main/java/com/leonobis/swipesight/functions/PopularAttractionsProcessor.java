package com.leonobis.swipesight.functions;

import com.leonobis.swipesight.models.Swipe;
import com.leonobis.swipesight.models.metrics.AttractionWithLikeCount;
import com.leonobis.swipesight.models.metrics.PopularAttractions;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PopularAttractionsProcessor extends ProcessAllWindowFunction<Swipe, PopularAttractions, TimeWindow> {
    @Override
    public void process(
            Context context,
            Iterable<Swipe> elements,
            Collector<PopularAttractions> out
    ) throws Exception {
        Map<Long, Long> likeCounts = new HashMap<>();

        for (Swipe swipe : elements) {
            if (swipe.getSwipeType().equalsIgnoreCase("like")) {
                likeCounts.put(swipe.getAttractionId(), likeCounts.getOrDefault(swipe.getAttractionId(), 0L) + 1);
            }
        }

        List<AttractionWithLikeCount> attractionLikeCounts = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : likeCounts.entrySet()) {
            attractionLikeCounts.add(new AttractionWithLikeCount(
                    entry.getKey(),
                    entry.getValue()
            ));
        }

        attractionLikeCounts.sort((a, b) -> Long.compare(b.likeCount, a.likeCount));

        List<AttractionWithLikeCount> top5 = attractionLikeCounts.stream()
                .limit(5)
                .collect(Collectors.toList());

        PopularAttractions popularAttractions = new PopularAttractions(
                System.currentTimeMillis(),
                context.window().getStart(),
                context.window().getEnd(),
                top5
        );

        out.collect(popularAttractions);
    }
}
