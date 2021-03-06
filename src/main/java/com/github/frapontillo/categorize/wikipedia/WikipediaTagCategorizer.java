package com.github.frapontillo.categorize.wikipedia;

import com.github.frapontillo.categorize.wikipedia.rest.WikipediaResponse;
import com.github.frapontillo.pulse.crowd.categorize.ITagCategorizerOperator;
import com.github.frapontillo.pulse.crowd.data.entity.Category;
import com.github.frapontillo.pulse.crowd.data.entity.Message;
import com.github.frapontillo.pulse.crowd.data.entity.Tag;
import com.github.frapontillo.pulse.spi.IPlugin;
import com.github.frapontillo.pulse.spi.IPluginConfig;
import com.github.frapontillo.pulse.spi.PluginConfigHelper;
import com.github.frapontillo.pulse.util.PulseLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.github.frapontillo.categorize.wikipedia.rest.WikipediaResponseDeserializer;
import com.github.frapontillo.categorize.wikipedia.rest.WikipediaService;
import com.google.gson.JsonElement;
import org.apache.logging.log4j.Logger;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;
import rx.Observable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Francesco Pontillo
 */
public class WikipediaTagCategorizer extends IPlugin<Message, Message, WikipediaTagCategorizer.WikipediaTagCategorizerConfig> {
    public static final String PLUGIN_NAME = "wikipedia";
    private static final String WIKIPEDIA_ENDPOINT_1 = "http://";
    private static final String WIKIPEDIA_ENDPOINT_2 = ".wikipedia.org/w";
    private final Logger logger = PulseLogger.getLogger(WikipediaTagCategorizer.class);

    private Gson gson;
    private Map<String, WikipediaService> wikipediaServiceMap;

    public WikipediaTagCategorizer() {
        wikipediaServiceMap = new HashMap<>();
    }

    @Override public String getName() {
        return PLUGIN_NAME;
    }

    @Override public WikipediaTagCategorizerConfig getNewParameter() {
        return new WikipediaTagCategorizerConfig();
    }

    @Override public Observable.Operator<Message, Message> getOperator(WikipediaTagCategorizerConfig parameters) {
        return new ITagCategorizerOperator(this) {
            @Override public List<Category> getCategories(Tag tag) {
                WikipediaService wikipediaService = getService(tag.getLanguage());
                try {

                    if (parameters != null) {
                        switch (parameters.getCalculate()) {
                            case WikipediaTagCategorizerConfig.ALL:
                                return wikipediaService.tag(tag.getText()).getCategories();

                            case WikipediaTagCategorizerConfig.NEW:
                                if (tag.getCategories() == null || tag.getCategories().size() == 0) {
                                    return wikipediaService.tag(tag.getText()).getCategories();
                                } else {
                                    logger.info("Tag skipped (categories already exist)");
                                    return null;
                                }
                            default:
                                return wikipediaService.tag(tag.getText()).getCategories();
                        }
                    }

                    return wikipediaService.tag(tag.getText()).getCategories();
                } catch (RetrofitError error) {
                    logger.error(error);
                    return null;
                }
            }
        };
    }

    /**
     * Get or create a {@link WikipediaService} for the specified language.
     *
     * @param language The language for the Wiki service.
     *
     * @return A {@link WikipediaService} instance.
     */
    private WikipediaService getService(String language) {
        WikipediaService wikipediaService = wikipediaServiceMap.get(language);
        if (wikipediaService == null) {
            // build the Gson deserializers collection
            if (gson == null) {
                gson = new GsonBuilder().registerTypeAdapter(WikipediaResponse.class,
                        new WikipediaResponseDeserializer()).create();
            }
            // build the REST client
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(WIKIPEDIA_ENDPOINT_1 + language + WIKIPEDIA_ENDPOINT_2)
                    .setConverter(new GsonConverter(gson)).build();
            wikipediaService = restAdapter.create(WikipediaService.class);
            wikipediaServiceMap.put(language, wikipediaService);
        }
        return wikipediaService;
    }

    public class WikipediaTagCategorizerConfig implements IPluginConfig<WikipediaTagCategorizerConfig> {

        /**
         * Categorize of all tags coming from the stream.
         */
        public static final String ALL = "all";

        /**
         * Categorize the tags with no tags (property is null or array is empty).
         */
        public static final String NEW = "new";


        private String calculate;

        @Override public WikipediaTagCategorizerConfig buildFromJsonElement(JsonElement jsonElement) {
            return PluginConfigHelper.buildFromJson(jsonElement, WikipediaTagCategorizerConfig.class);
        }

        public String getCalculate() {
            return calculate;
        }

        public String setCalculate() {
            return calculate;
        }
    }
}
