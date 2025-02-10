package com.leonobis.swipesight.functions;

import com.leonobis.swipesight.models.*;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.streaming.util.ProcessFunctionTestHarnesses;
import org.apache.flink.streaming.util.TwoInputStreamOperatorTestHarness;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test verifies the recommendation generation.
 */
public class ContentBasedRecommenderTest {

    @Test
    public void testingContentBasedRecommender() throws Exception {
        ContentBasedRecommender contentBasedRecommender = new ContentBasedRecommender(new Random(7));

        TwoInputStreamOperatorTestHarness<NearbyAttractions, UserLikeProbs, RecommendationItem> testHarness =
                ProcessFunctionTestHarnesses.forKeyedCoProcessFunction(
                        contentBasedRecommender,
                        NearbyAttractions::getUserId,
                        UserLikeProbs::getUserId,
                        BasicTypeInfo.INT_TYPE_INFO
                );

        NearbyAttractions nearbyAttractions = new NearbyAttractions(
                1,
                buildTestAttractions()
        );

        UserLikeProbs userLikeProbs = new UserLikeProbs(
                1L,
                1,
                Map.of("tourism:attraction", 1.0, "tourism:viewpoint", 0.0)
        );

        testHarness.processElement1(nearbyAttractions, 1L);
        testHarness.processElement2(userLikeProbs, 2L);

        Map<String, String> expected = Map.of("tourism", "attraction");
        Map<String, String> actual = testHarness.extractOutputValues().get(1).getAttractionData().getTags();

        assertEquals(expected, actual);
    }

    List<Attraction> buildTestAttractions() {
        ArrayList<Attraction> testAttractions = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            Map<String, String> tags;
            if (i < 2) {
                tags = Map.of("tourism", "attraction");
            } else {
                tags = Map.of("tourism", "viewpoint");
            }

            Attraction attraction = new Attraction(
                    (long) i,
                    1f,
                    "attraction" + i,
                    1f,
                    (long) i,
                    1f,
                    tags
            );

            testAttractions.add(attraction);
        }

        return testAttractions;
    }

}