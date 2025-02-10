CREATE TABLE swipes
(
    attractionId       BIGINT,
    userId           BIGINT,
    swipeType TEXT
) WITH (
      'connector' = 'kafka',
      'topic' = 'swipes',
      'properties.bootstrap.servers' = 'kafka:9092',
      'scan.startup.mode' = 'earliest-offset',
      'format' = 'json'
      );