/*
    SQL command to get distinct key-value pairs

    SELECT DISTINCT key, value
    FROM attractions, jsonb_each_text(tags);
 */
export const categories = [
    "amenity:fountain",
    "building:bunker",
    "building:castle",
    "building:church",
    "building:pagoda",
    "building:ruins",
    "building:tower",
    "building:windmill",
    "historic:castle",
    "historic:monument",
    "leisure:garden",
    "leisure:nature_reserve",
    "leisure:park",
    "tourism:aquarium",
    "tourism:artwork",
    "tourism:attraction",
    "tourism:gallery",
    "tourism:museum",
    "tourism:picnic_site",
    "tourism:theme_park",
    "tourism:viewpoint",
    "tourism:wilderness_hut",
    "tourism:zoo"
];