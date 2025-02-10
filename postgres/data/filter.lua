json = require "json"

local attractions = osm2pgsql.define_table({
    name = "attractions",
    ids = { type = "any", type_column = "osm_type", id_column = "osm_id" },
    columns = {
        { column = "id", sql_type = "serial", create_only = true },
        { column = "name", type = "text" },
        { column = "tags", sql_type = "jsonb" }, -- Use JSONB for tags
        { column = "geom", type = "point", projection = 4326, not_null = true }, -- do not rename
        { column = "geom_3857", type = "point", projection = 3857, not_null = true } -- do not rename
    }
})

-- Define a whitelist for each tag category
local attraction_whitelist = {
    tourism = {
        "aquarium",
        "artwork",
        "attraction",
        "gallery",
        "museum",
        "picnic_site",
        "theme_park",
        "viewpoint",
        "wilderness_hut",
        "zoo",
    },
    building = {
        "castle",
        "church",
        "ruins",
        "tower",
        "windmill",
        "pagoda",
        "bunker"
    },
    historic = {
        "castle",
        "monument"
    },
    leisure = {
        "park",
        "garden",
        "nature_reserve"
    },
    amenity = {
        "fountain"
    }
}

function is_attraction(object)
    -- Check if any relevant tag is present and has a valid value
    for key, valid_values in pairs(attraction_whitelist) do
        if object.tags[key] then
            for _, valid_value in ipairs(valid_values) do
                if object.tags[key] == valid_value then
                    -- Ensure the object has a name and it's not empty
                    if object.tags.name and object.tags.name ~= "" then
                        return object
                    end
                end
            end
        end
    end
end

function filter_tags(tags)
    -- Check if the tags table is nil or empty
    if not tags or next(tags) == nil then
        return nil -- Return nil for no tags
    end

    local table = {}

    -- Iterate over the whitelist to include only relevant tags and values
    for key, valid_values in pairs(attraction_whitelist) do
        if tags[key] then
            -- Check if the value is in the whitelist for this key
            for _, valid_value in ipairs(valid_values) do
                if tags[key] == valid_value then
                    table[key] = tags[key] -- Add key-value pair to JSON object
                    break -- Found a match, no need to check further for this key
                end
            end
        end
    end

    return json.encode(table) -- Return the constructed JSON object
end

function osm2pgsql.process_node(object)
    if is_attraction(object) then
        local row = {
            name = object.tags.name,
            tags = filter_tags(object.tags),
            geom = object:as_point(),
            geom_3857 = object:as_point()
        }
        attractions:insert(row)
    end
end

function osm2pgsql.process_way(object)
    if is_attraction(object) and object.is_closed then
        local row = {
            name = object.tags.name,
            tags = filter_tags(object.tags), -- Use JSON-formatted tags
            geom = object:as_multipolygon():centroid(),
            geom_3857 = object:as_multipolygon():centroid()
        }
        attractions:insert(row)
    end
end

function osm2pgsql.process_relation(object)
    if is_attraction(object) and object.tags.type == "multipolygon" then
        local row = {
            name = object.tags.name,
            tags = filter_tags(object.tags), -- Use JSON-formatted tags
            geom = object:as_multipolygon():centroid(),
            geom_3857 = object:as_multipolygon():centroid()
        }
        attractions:insert(row)
    end
end
