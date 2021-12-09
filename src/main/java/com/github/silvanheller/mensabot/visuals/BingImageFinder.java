package com.github.silvanheller.mensabot.visuals;

import com.github.silvanheller.mensabot.config.BotConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.microsoft.bing.imagesearch.implementation.ImageSearchClientImpl;
import com.microsoft.bing.imagesearch.models.ImageObject;
import com.microsoft.rest.credentials.ServiceClientCredentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.List;

public class BingImageFinder extends ImageFinder {

    private static final Type LIST_TYPE = new TypeToken<List<ImageObject>>() {
    }.getType();
    private static final Logger LOGGER = LogManager.getLogger();
    private ImageSearchClientImpl imgSearchClient;
    private boolean init = false;
    private static final Gson gson = new Gson();
    private static final OkHttpClient okHttpClient = new OkHttpClient();


    public BingImageFinder() {
        try {
            // Authenticate
            // Set the BING_SEARCH_V7_SUBSCRIPTION_KEY environment variable,
            // then reopen your command prompt or IDE for changes to take effect.
            final String subscriptionKey = BotConfig.BING_TOKEN;
            // Add your Bing Search V7 endpoint to your environment variables.
            String endpoint = "https://api.bing.microsoft.com/v7.0";

            ServiceClientCredentials credentials = builder -> builder.addNetworkInterceptor(
                    chain -> {
                        Request request = null;
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder();
                        requestBuilder.addHeader("Ocp-Apim-Subscription-Key", subscriptionKey);
                        request = requestBuilder.build();
                        return chain.proceed(request);
                    }
            );
            imgSearchClient = new ImageSearchClientImpl(endpoint, credentials);
            init = true;

        } catch (Exception e) {
            LOGGER.error("Initialization of Bing Image Finder failed");
        }
    }

    @Override
    protected String fetchThumbURLForFood(String food) {
        if (!init) {
            // no initialization = no queries
            return null;
        }
        List<ImageObject> images = null;
        var storage = Paths.get("storage", food, "response.json");
        if (storage.toFile().exists()) {
            LOGGER.debug("cache-hit");
            JsonReader reader = null;
            try {
                reader = new JsonReader(new FileReader(storage.toFile()));
                images = gson.fromJson(reader, LIST_TYPE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            var results = imgSearchClient.images().search(food);
            if (results == null) {
                LOGGER.warn("null as a result from bing API");
                return null;
            }
            images = results.value();
        }
        if (images.size() == 0) {
            LOGGER.warn("no results for food: " + food);
            return null;
        }
        List<ImageObject> finalImages = images;
        new Thread(() -> storeResults(finalImages, food)).start();
        return images.get(0).thumbnailUrl();
    }

    private void storeResults(List<ImageObject> results, String query) {
        try {
            var storage = Paths.get("storage", query);
            storage.toFile().mkdirs();
            var jsonPath = storage.resolve("response.json");
            var fw = new FileWriter(jsonPath.toFile());
            gson.toJson(results, fw);
            fw.flush();
            fw.close();
            for (ImageObject img : results) {
                var imagePath = storage.resolve(img.imageId() + ".jpg");
                var request = new Request.Builder().url(img.thumbnailUrl()).build();
                var response = okHttpClient.newCall(request).execute();
                InputStream inputStream = response.body().byteStream();
                BufferedImage image = ImageIO.read(inputStream);
                ImageIO.write(image, "jpg", imagePath.toFile());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
