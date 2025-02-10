package com.leonobis.swipesight.functions;

import com.leonobis.swipesight.models.Swipe;
import com.leonobis.swipesight.models.TagStats;
import com.leonobis.swipesight.models.UserLikeProbs;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class generates user interest profiles by analyzing user interactions.
 */
public class LikeProbsExtractor extends KeyedProcessFunction<Integer, Swipe, UserLikeProbs> {
    private MapState<String, TagStats> tagStatsState;
    private MapState<String, Double> likeProbsState;

    @Override
    public void open(OpenContext openContext) throws Exception {
        tagStatsState = getRuntimeContext().getMapState(new MapStateDescriptor<>("tagStatsState", String.class, TagStats.class));
        likeProbsState = getRuntimeContext().getMapState(new MapStateDescriptor<>("likeProbsState", String.class, Double.class));
    }

    @Override
    public void processElement(
            Swipe swipe,
            Context ctx,
            Collector<UserLikeProbs> out
    ) throws Exception {
        String swipeType = swipe.getSwipeType();

        Map.Entry<String, String> firstTag = swipe.getAttractionTags().entrySet().stream().findFirst().orElse(null);

        if (firstTag != null) {
            String tag = firstTag.getKey() + ":" + firstTag.getValue();
            TagStats stats = Optional.ofNullable(tagStatsState.get(tag)).orElse(new TagStats());

            if (swipeType.equalsIgnoreCase("like")) {
                stats.incrementLikes();
            } else if (swipeType.equalsIgnoreCase("dislike")) {
                stats.incrementDislikes();
            }

            tagStatsState.put(tag, stats);

            if (stats.getLikes() > 0) {
                likeProbsState.put(tag, stats.getLikeProbability());
            }
        }

        Map<String, Double> likeProbs = new HashMap<>();
        for (Map.Entry<String, Double> entry : likeProbsState.entries()) {
            likeProbs.put(entry.getKey(), entry.getValue());
        }

        out.collect(new UserLikeProbs(ctx.timestamp(), ctx.getCurrentKey(), likeProbs));
    }
}
