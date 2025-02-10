CREATE TABLE IF NOT EXISTS attractions
(
    osm_type character(1) NOT NULL,
    osm_id bigint NOT NULL,
    id SERIAL PRIMARY KEY ,
    name text,
    tags jsonb,
    geom geometry(Point,4326) NOT NULL,
    geom_3857 geometry
);

create
    or replace function attractions_by_location(user_lat float, user_lon float, radius float)
    returns table
            (
                id          attractions.id%TYPE,
                osm_id      attractions.osm_id%TYPE,
                name        attractions.name%TYPE,
                tags        attractions.tags%TYPE,
                lat         float,
                lon         float,
                dist_meters float
            )
    language sql
as
$$
WITH user_geom AS (
    SELECT ST_Transform(ST_SetSRID(ST_MakePoint(user_lon, user_lat), 4326), 3857) AS user_point
)
SELECT id,
       osm_id,
       name,
       tags,
       ST_Y(geom) AS lat,
       ST_X(geom) AS lon,
       ST_Distance(geom_3857, user_geom.user_point) AS dist_meters
FROM attractions, user_geom
WHERE ST_DWithin(
              geom_3857,
              user_geom.user_point,
              radius
      )
ORDER BY dist_meters;
$$;