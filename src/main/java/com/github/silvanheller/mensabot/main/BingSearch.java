package com.github.silvanheller.mensabot.main;

import com.microsoft.bing.imagesearch.models.ImageObject;
import com.microsoft.bing.imagesearch.models.Images;
import com.microsoft.bing.imagesearch.implementation.ImageSearchClientImpl;
import com.microsoft.rest.credentials.ServiceClientCredentials;
import okhttp3.*;
import okhttp3.OkHttpClient.Builder;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import com.github.silvanheller.mensabot.config.BotConfig;

public class BingSearch {

    /**
     * Main function which runs the actual sample.
     *
     * @param client instance of the Bing News Search API client
     * @param searchTerm a term to use in the image search request
     * @return true if sample runs successfully
     */
    public static void runSample(ImageSearchClientImpl client, String searchTerm) {
        try {

            //=============================================================
            // This will search images for "canadian rockies" then print the first image result,

            System.out.println(String.format("Search images for query %s", searchTerm));

            Images imageResults = client.images().search(searchTerm);

            if (imageResults != null && imageResults.value().size() > 0) {
                // Image results
                ImageObject firstImageResult = imageResults.value().get(0);

                System.out.println(String.format("Image result count: %d", imageResults.value().size()));
                System.out.println(String.format("First image insights token: %s", firstImageResult.imageInsightsToken()));
                System.out.println(String.format("First image thumbnail url: %s", firstImageResult.thumbnailUrl()));
                System.out.println(String.format("First image content url: %s", firstImageResult.contentUrl()));
                System.out.print(imageResults.value().size()+ " results");
            }
            else {
                System.out.println("Couldn't find any image results!");
            }
        }
        catch (Exception f) {
            System.out.println(f.getMessage());
            f.printStackTrace();
        }
    }

    /**
     * Main entry point.
     *
     * @param args the parameters
     */
    public static void main(String[] args) {
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
            ImageSearchClientImpl client = new ImageSearchClientImpl(endpoint,credentials);
            String searchTerm = "Wienerli";
            runSample(client, searchTerm);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
