package com.github.frapontillo.categorize.wikipedia.rest;

import com.github.frapontillo.pulse.crowd.data.entity.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class WikipediaResponse {
    private List<Category> categories;

    public WikipediaResponse() {
        categories = new ArrayList<>();
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
