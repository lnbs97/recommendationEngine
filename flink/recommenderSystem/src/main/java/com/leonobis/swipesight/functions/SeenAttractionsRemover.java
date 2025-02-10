package com.leonobis.swipesight.functions;

import com.leonobis.swipesight.models.Attraction;
import com.leonobis.swipesight.models.NearbyAttractions;
import com.leonobis.swipesight.models.Swipe;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class removes seen attractions from the stream by
 * keeping track of all attractions that a user has interacted with.
 */
public class SeenAttractionsRemover extends KeyedCoProcessFunction<
        Integer,
        NearbyAttractions,
        Swipe,
        NearbyAttractions
        > {
    ListState<Long> seenAttractionIdsState;

    @Override
    public void open(OpenContext openContext) throws Exception {
        seenAttractionIdsState = getRuntimeContext().getListState(
                new ListStateDescriptor<>("seenAttractionIdsState", Long.class)
        );
    }

    @Override
    public void processElement1(
            NearbyAttractions value,
            Context ctx,
            Collector<NearbyAttractions> out
    ) throws Exception {
        List<Attraction> attractions = value.getNearbyAttractions();

        List<Long> pastIds = new ArrayList<>();
        for (Long id : seenAttractionIdsState.get()) {
            pastIds.add(id);
        }

        List<Attraction> filteredAttractions = attractions.stream()
                .filter(attraction -> !pastIds.contains(attraction.getId()))
                .collect(Collectors.toList());

        out.collect(new NearbyAttractions(ctx.getCurrentKey(), filteredAttractions));
    }

    @Override
    public void processElement2(
            Swipe swipe,
            Context ctx,
            Collector<NearbyAttractions> out
    ) throws Exception {
        seenAttractionIdsState.add(swipe.getAttractionId());
    }
}
