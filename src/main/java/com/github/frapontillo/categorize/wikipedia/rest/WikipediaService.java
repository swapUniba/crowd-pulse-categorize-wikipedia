package com.github.frapontillo.categorize.wikipedia.rest;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * @author Francesco Pontillo
 */
public interface WikipediaService {
    @GET("/api.php?action=query&prop=categories&format=json&continue=")
    WikipediaResponse tag(@Query("titles") String text);
}
