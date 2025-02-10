package com.leonobis.swipesight.functions;

import com.leonobis.swipesight.models.Swipe;
import com.leonobis.swipesight.models.UserLikeProbs;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.streaming.util.OneInputStreamOperatorTestHarness;
import org.apache.flink.streaming.util.ProcessFunctionTestHarnesses;
import org.junit.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test verifies that the {@link LikeProbsExtractor}
 * generates correct {@link UserLikeProbs}.
 */
public class LikeProbsExtractorTest {

    @Test
    public void testingLikeProbsExtractor() throws Exception {
        LikeProbsExtractor likeProbsExtractor = new LikeProbsExtractor();

        OneInputStreamOperatorTestHarness<Swipe, UserLikeProbs> testHarness = ProcessFunctionTestHarnesses.forKeyedProcessFunction(
                likeProbsExtractor,
                Swipe::getUserId,
                BasicTypeInfo.INT_TYPE_INFO
        );

        testHarness.processElement(new Swipe(
                1L,
                "abcd",
                1L,
                "like",
                1,
                Map.of("tourism", "attraction")
        ), 1L);

        testHarness.processElement(new Swipe(
                2L,
                "1234",
                2L,
                "dislike",
                1,
                Map.of("tourism", "attraction")
        ), 2L);

        UserLikeProbs expected =
                new UserLikeProbs(
                        2L,
                        1,
                        Map.of("tourism:attraction", 0.5)
                );
        UserLikeProbs actual = testHarness.extractOutputValues().get(1);

        assertEquals(expected, actual);
    }

}