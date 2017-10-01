package com.github.frapontillo.categorize.wikipedia.rest;

import com.github.frapontillo.pulse.crowd.data.entity.Category;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

/**
 * @author Francesco Pontillo
 */
public class WikipediaResponseDeserializer implements JsonDeserializer<WikipediaResponse> {

    @Override public WikipediaResponse deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {

        WikipediaResponse response = new WikipediaResponse();

        // get the main query object
        JsonElement queryElement = json.getAsJsonObject().get("query");
        if (queryElement == null) {
            return response;
        }
        // get the pages set
        Set<Map.Entry<String, JsonElement>> pages =
                queryElement.getAsJsonObject().get("pages").getAsJsonObject().entrySet();

        // for each page, retrieve and add all categories
        for (Map.Entry<String, JsonElement> page : pages) {
            if (page.getKey().equals("-1")) {
                continue;
            }
            JsonElement categories = page.getValue().getAsJsonObject().get("categories");
            if (categories != null) {
                JsonArray jsonCategories = categories.getAsJsonArray();
                for (JsonElement jsonCategory : jsonCategories) {
                    response.getCategories().add(new Category(
                            jsonCategory.getAsJsonObject().get("title").getAsString()));
                }
            }
        }

        return response;
    }
}