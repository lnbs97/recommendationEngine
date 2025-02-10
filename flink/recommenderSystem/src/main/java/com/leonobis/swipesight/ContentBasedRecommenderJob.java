/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.leonobis.swipesight;

import com.leonobis.swipesight.functions.*;
import com.leonobis.swipesight.models.*;
import com.leonobis.swipesight.models.metrics.LongValueMetric;
import com.leonobis.swipesight.models.metrics.PopularAttractions;
import com.leonobis.swipesight.util.Config;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.formats.json.JsonDeserializationSchema;
import org.apache.flink.formats.json.JsonSerializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;

import java.time.Duration;
import java.util.Random;

/**
 * This job generates recommendations and metrics.
 */
public class ContentBasedRecommenderJob {

    private final KafkaSource<Swipe> swipeSource;
    private final KafkaSource<Location> locationSource;
    private final KafkaSink<RecommendationItem> recommendationItemSink;
    private final KafkaSink<UserLikeProbs> userLikeProbsSink;
    private final KafkaSink<LongValueMetric> swipeCountSink;
    private final KafkaSink<PopularAttractions> topAttractionsKafkaSink;

    public ContentBasedRecommenderJob(
            KafkaSource<Swipe> swipeSource,
            KafkaSource<Location> locationSource,
            KafkaSink<RecommendationItem> recommendationItemSink,
            KafkaSink<UserLikeProbs> userLikeProbsSink,
            KafkaSink<LongValueMetric> swipeCountSink,
            KafkaSink<PopularAttractions> topAttractionsKafkaSink
    ) {
        this.swipeSource = swipeSource;
        this.locationSource = locationSource;
        this.userLikeProbsSink = userLikeProbsSink;
        this.recommendationItemSink = recommendationItemSink;
        this.swipeCountSink = swipeCountSink;
        this.topAttractionsKafkaSink = topAttractionsKafkaSink;
    }

    public static void main(String[] args) throws Exception {
        // SOURCES
        KafkaSource<Swipe> swipeSource = createSwipeSource();
        KafkaSource<Location> locationSource = createLocationSource();

        // SINKS
        KafkaSink<RecommendationItem> recommendationItemSink = createRecommendationSink();
        KafkaSink<UserLikeProbs> userLikeProbsSink = createUserLikeProbsSink();
        KafkaSink<LongValueMetric> swipeCountMetricsKafkaSink = createSwipeCountMetricsSink();
        KafkaSink<PopularAttractions> topAttractionsKafkaSink = createTopAttractionsMetricsSink();

        ContentBasedRecommenderJob job =
                new ContentBasedRecommenderJob(
                        swipeSource,
                        locationSource,
                        recommendationItemSink,
                        userLikeProbsSink,
                        swipeCountMetricsKafkaSink,
                        topAttractionsKafkaSink
                );

        job.execute();
    }

    private static KafkaSink<PopularAttractions> createTopAttractionsMetricsSink() {
        JsonSerializationSchema<PopularAttractions> topAttractionsFormat = new JsonSerializationSchema<>();
        return KafkaSink.<PopularAttractions>builder()
                .setBootstrapServers(Config.getKafkaBootstrapServers())
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic("metrics.top-attractions")
                        .setValueSerializationSchema(topAttractionsFormat)
                        .build())
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();
    }

    private static KafkaSink<LongValueMetric> createSwipeCountMetricsSink() {
        JsonSerializationSchema<LongValueMetric> swipeCountMetricsFormat = new JsonSerializationSchema<>();
        return KafkaSink.<LongValueMetric>builder()
                .setBootstrapServers(Config.getKafkaBootstrapServers())
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic("metrics.swipe-count")
                        .setValueSerializationSchema(swipeCountMetricsFormat)
                        .build())
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();
    }

    private static KafkaSink<UserLikeProbs> createUserLikeProbsSink() {
        return KafkaSink.<UserLikeProbs>builder()
                .setBootstrapServers(Config.getKafkaBootstrapServers())
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic("metrics.like-probs")
                        .setValueSerializationSchema(new JsonSerializationSchema<UserLikeProbs>())
                        .build())
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();
    }

    private static KafkaSink<RecommendationItem> createRecommendationSink() {
        return KafkaSink.<RecommendationItem>builder()
                .setBootstrapServers(Config.getKafkaBootstrapServers())
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic("recommendations")
                        .setValueSerializationSchema(new JsonSerializationSchema<RecommendationItem>())
                        .build())
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();
    }

    private static KafkaSource<Location> createLocationSource() {
        JsonDeserializationSchema<Location> locationFormat = new JsonDeserializationSchema<>(Location.class);
        return KafkaSource.<Location>builder()
                .setBootstrapServers(Config.getKafkaBootstrapServers())
                .setTopics("locations")
                .setGroupId("flink-group")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(locationFormat)
                .build();
    }

    private static KafkaSource<Swipe> createSwipeSource() {
        JsonDeserializationSchema<Swipe> swipeFormat = new JsonDeserializationSchema<>(Swipe.class);
        return KafkaSource.<Swipe>builder()
                .setBootstrapServers(Config.getKafkaBootstrapServers())
                .setTopics("swipes")
                .setGroupId("flink-group")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(swipeFormat)
                .build();
    }

    public JobExecutionResult execute() throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        WatermarkStrategy<Swipe> watermarkStrategy = WatermarkStrategy
                .<Swipe>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                .withTimestampAssigner((swipe, recordTimestamp) -> swipe.getEventTime())
                .withIdleness(Duration.ofSeconds(10));

        // SOURCE STREAMS
        DataStream<Swipe> swipeStream = env.fromSource(
                swipeSource,
                watermarkStrategy,
                "Swipe Source"
        );

        DataStream<Location> locationStream = env.fromSource(
                locationSource,
                WatermarkStrategy.noWatermarks(),
                "Location Source"
        );

        // INTERMEDIATE STREAMS
        DataStream<LongValueMetric> swipeCountPerSecond = swipeStream
                .windowAll(TumblingProcessingTimeWindows.of(Duration.ofSeconds(1)))
                .apply(new SwipeCountProcessor());

        DataStream<PopularAttractions> top5Attractions = swipeStream
                .windowAll(SlidingEventTimeWindows.of(Duration.ofMinutes(15), Duration.ofSeconds(5)))
                .process(new PopularAttractionsProcessor());

        DataStream<UserLikeProbs> userLikeProbs = swipeStream
                .keyBy(Swipe::getUserId)
                .process(new LikeProbsExtractor());

        DataStream<NearbyAttractions> nearbyAttractions = locationStream
                .keyBy(Location::getUserId)
                .process(new NearbyAttractionsProcessor());

        DataStream<NearbyAttractions> recommendationCandidates = nearbyAttractions
                .keyBy(NearbyAttractions::getUserId)
                .connect(swipeStream.keyBy(Swipe::getUserId))
                .process(new SeenAttractionsRemover());

        DataStream<RecommendationItem> recommendationItems = recommendationCandidates
                .keyBy(NearbyAttractions::getUserId)
                .connect(userLikeProbs.keyBy(UserLikeProbs::getUserId))
                .process(new ContentBasedRecommender(new Random()));


        // STREAM SINKING
        userLikeProbs.sinkTo(userLikeProbsSink);
        swipeCountPerSecond.sinkTo(swipeCountSink);
        top5Attractions.sinkTo(topAttractionsKafkaSink);
        recommendationItems.sinkTo(recommendationItemSink);

        return env.execute("Flink Recommender Job");
    }

}
