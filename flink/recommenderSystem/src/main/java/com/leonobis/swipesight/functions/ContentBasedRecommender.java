package com.leonobis.swipesight.functions;

import com.leonobis.swipesight.models.*;
import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;
import org.apache.flink.util.Collector;

import java.util.*;
import java.util.stream.Collectors;

import static com.leonobis.swipesight.models.RecommendationItem.RecommendationType.CONTENT_BASED_FILTERING;

/**
 * This class generates recommendations based on user like probabilities and nearby attractions.
 */
public class ContentBasedRecommender extends KeyedCoProcessFunction<
        Integer,
        NearbyAttractions,
        UserLikeProbs,
        RecommendationItem
        > {
    Random random;

    ValueState<Boolean> isFirstLaunchState;
    ValueState<UserLikeProbs> likeProbsState;
    MapState<String, List<Attraction>> attractionsByTagState;

    /*
    Injecting Random for better testability
     */
    public ContentBasedRecommender(Random random) {
        this.random = random;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        isFirstLaunchState = getRuntimeContext().getState(new ValueStateDescriptor<>("isFirstLaunch", Boolean.class));
        likeProbsState = getRuntimeContext().getState(new ValueStateDescriptor<>("likeProbsState", UserLikeProbs.class));

        MapStateDescriptor<String, List<Attraction>> attractionsByTagStateDescriptor = new MapStateDescriptor<>(
                "attractionsByTagState",
                TypeInformation.of(String.class),
                new TypeHint<List<Attraction>>() {
                }.getTypeInfo()
        );

        attractionsByTagState = getRuntimeContext().getMapState(attractionsByTagStateDescriptor);
    }

    @Override
    public void processElement1(
            NearbyAttractions nearbyAttractions,
            Context ctx,
            Collector<RecommendationItem> out
    ) throws Exception {
        Map<String, List<Attraction>> tagToAttractions = indexAttractionsByTag(nearbyAttractions.getNearbyAttractions());
        attractionsByTagState.putAll(tagToAttractions);

        if (isFirstLaunchState.value() == null) {
            // generate initial recommendation on first launch
            isFirstLaunchState.update(false);
            Long userId = Long.valueOf(ctx.getCurrentKey());
            UserLikeProbs likeProbs = new UserLikeProbs();
            RecommendationItem recommendationItem = generateRecommendationItem(likeProbs, userId);
            out.collect(recommendationItem);
        }
    }

    @Override
    public void processElement2(
            UserLikeProbs likeProbs,
            Context ctx,
            Collector<RecommendationItem> out
    ) throws Exception {
        likeProbsState.update(likeProbs);
        Long userId = Long.valueOf(ctx.getCurrentKey());
        RecommendationItem recommendationItem = generateRecommendationItem(likeProbs, userId);
        out.collect(recommendationItem);
    }

    private RecommendationItem generateRecommendationItem(
            UserLikeProbs likeProbs,
            Long userId
    ) throws Exception {
        Map<String, Double> likeProbsMap = likeProbs.getLikeProbs();

        List<String> availableTags = new ArrayList<>();
        attractionsByTagState.keys().forEach(availableTags::add);
        Set<String> exhaustedTags = new HashSet<>();

        final int maxAttempts = 50;

        for (int attempts = 0; attempts < maxAttempts; attempts++) {
            List<String> nonExhaustedTags = availableTags.stream()
                    .filter(tag -> !exhaustedTags.contains(tag))
                    .collect(Collectors.toList());

            if (nonExhaustedTags.isEmpty()) {
                throw new Exception("No recommendations available for user " + userId);
            }

            String selectedTag = getRecommendationTag(nonExhaustedTags, likeProbsMap);

            List<Attraction> taggedAttractions = new ArrayList<>(attractionsByTagState.get(selectedTag));

            if (!taggedAttractions.isEmpty()) {
                Attraction attraction = taggedAttractions.remove(0);
                attractionsByTagState.put(selectedTag, taggedAttractions);

                Map<String, Object> recommendationData = new HashMap<>();
                recommendationData.put("recommendation_type", CONTENT_BASED_FILTERING);
                recommendationData.put("like_probability", likeProbsMap.get(selectedTag));
                recommendationData.put("category", selectedTag);

                // Remove tag from map if exhausted
                if (taggedAttractions.isEmpty()) {
                    exhaustedTags.add(selectedTag);
                }

                // Return recommendation
                return new RecommendationItem(userId, recommendationData, attraction);
            } else {
                // Mark tag as exhausted
                exhaustedTags.add(selectedTag);
            }
        }
        throw new Exception("No valid recommendation found after " + maxAttempts + " attempts for user " + userId);
    }

    private Map<String, List<Attraction>> indexAttractionsByTag(List<Attraction> attractions) {
        Map<String, List<Attraction>> tagToAttractions = new HashMap<>();
        for (Attraction attraction : attractions) {
            for (String tag : attraction.getTagsAsStrings()) {
                tagToAttractions.computeIfAbsent(tag, k -> new ArrayList<>()).add(attraction);
            }
        }
        return tagToAttractions;
    }

    private String getRecommendationTag(
            List<String> attractionTags,
            Map<String, Double> likeProbs
    ) {
        Map<String, Double> enrichedLikeProbs = getEnrichedLikeProbs(likeProbs, attractionTags);
        Map<String, Double> cumulativeProbs = getCumulativeProbs(enrichedLikeProbs);

        String selectedTag = selectTagFromCumulativeProbs(cumulativeProbs);

        while (!attractionTags.contains(selectedTag)) {
            selectedTag = selectTagFromCumulativeProbs(cumulativeProbs);
        }

        return selectedTag;
    }

    private String selectTagFromCumulativeProbs(Map<String, Double> cumulativeProbs) {
        double randomValue = random.nextDouble();
        return cumulativeProbs.entrySet().stream()
                .filter(entry -> randomValue < entry.getValue())
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(getLastKey(cumulativeProbs));
    }

    private Map<String, Double> getEnrichedLikeProbs(
            Map<String, Double> likeProbs,
            List<String> attractionTags
    ) {
        Map<String, Double> enrichedLikeProbs = new HashMap<>();

        for (String tag : attractionTags) {
            enrichedLikeProbs.put(tag, likeProbs.getOrDefault(tag, 1.0));
        }

        return enrichedLikeProbs;
    }

    private static <K, V> K getLastKey(Map<K, V> map) {
        return map.keySet().stream().reduce((first, second) -> second).orElse(null);
    }

    private Map<String, Double> getCumulativeProbs(Map<String, Double> likeProbsMap) {
        double probsSum = likeProbsMap.values().stream().mapToDouble(Double::doubleValue).sum();

        Map<String, Double> cumulativeProbs = new LinkedHashMap<>();
        double cumulativeSum = 0.0;

        for (Map.Entry<String, Double> entry : likeProbsMap.entrySet()) {
            double normalizedProb = entry.getValue() / probsSum;
            cumulativeSum += normalizedProb;
            cumulativeProbs.put(entry.getKey(), cumulativeSum);
        }
        return cumulativeProbs;
    }
}
