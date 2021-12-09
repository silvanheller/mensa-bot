package com.github.silvanheller.mensabot.visuals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public abstract class ImageFinder {

    private static final Logger LOGGER = LogManager.getLogger();
    private Map<String, String> urlCache = new HashMap<>();

    public String getThumbURLForFood(String food) {
        if (urlCache.containsKey(food)) {
            return urlCache.get(food);
        }
        String url = fetchThumbURLForFood(food);
        if (url == null || url.isEmpty()) {
            LOGGER.warn("no url for food {}", food);
            return null;
        }
        urlCache.put(food, url);
        return url;
    }

    protected abstract String fetchThumbURLForFood(String food);
}