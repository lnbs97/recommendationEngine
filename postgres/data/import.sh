osm2pgsql \
  --create \
  --host localhost \
  --database swipesight \
  --user admin \
  --port 5432 \
  --output flex \
  --style filter.lua \
  --slim \
  germany-latest.osm.pbf
