package com.leonobis.swipesight.functions;

import com.leonobis.swipesight.models.Attraction;
import com.leonobis.swipesight.models.Location;
import com.leonobis.swipesight.models.NearbyAttractions;
import com.leonobis.swipesight.service.AttractionService;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.List;

/**
 * This class emits nearby attractions by processing user locations.
 */
public class NearbyAttractionsProcessor extends KeyedProcessFunction<Integer, Location, NearbyAttractions> {
    @Override
    public void processElement(Location location, Context ctx, Collector<NearbyAttractions> out) throws Exception {
        List<Attraction> attractions = AttractionService.fetchAttractions(location.getLon(), location.getLat());
        out.collect(new NearbyAttractions(ctx.getCurrentKey(), attractions));
    }
}
