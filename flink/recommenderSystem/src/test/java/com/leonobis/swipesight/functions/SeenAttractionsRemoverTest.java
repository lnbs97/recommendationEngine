package com.leonobis.swipesight.functions;

import com.leonobis.swipesight.models.Attraction;
import com.leonobis.swipesight.models.NearbyAttractions;
import com.leonobis.swipesight.models.Swipe;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.streaming.util.ProcessFunctionTestHarnesses;
import org.apache.flink.streaming.util.TwoInputStreamOperatorTestHarness;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test verifies that the {@link SeenAttractionsRemover}
 * correctly removes attractions from the stream
 */
public class SeenAttractionsRemoverTest {

    @Test
    public void testingSeenAttractionsRemover() throws Exception {
        SeenAttractionsRemover seenAttractionsRemover = new SeenAttractionsRemover();

        TwoInputStreamOperatorTestHarness<NearbyAttractions, Swipe, NearbyAttractions> testHarness =
                ProcessFunctionTestHarnesses.forKeyedCoProcessFunction(
                        seenAttractionsRemover,
                        NearbyAttractions::getUserId,
                        Swipe::getUserId,
                        BasicTypeInfo.INT_TYPE_INFO
                );

        Attraction attraction1 = new Attraction(
                1L,
                1f,
                "attraction1",
                1f,
                1L,
                1f,
                Map.of("tourism", "attraction")
        );

        Attraction attraction2 = new Attraction(
                2L,
                1f,
                "attraction2",
                1f,
                2L,
                1f,
                Map.of("tourism", "attraction")
        );

        Swipe swipeForAttraction1 = new Swipe(
                1L,
                "abcd",
                1L,
                "like",
                1,
                Map.of("tourism", "attraction")
        );

        NearbyAttractions nearbyAttractions = new NearbyAttractions(
                1,
                List.of(attraction1, attraction2)
        );

        testHarness.processElement2(swipeForAttraction1, 1L);
        testHarness.processElement1(nearbyAttractions, 2L);

        NearbyAttractions expected = new NearbyAttractions(1, List.of(attraction2));
        NearbyAttractions actual = testHarness.extractOutputValues().get(0);

        assertEquals(expected, actual);
    }

}