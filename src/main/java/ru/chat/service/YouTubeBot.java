package ru.chat.service;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Component
public class YouTubeBot {

    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    public static final String KEY = "AIzaSyD30ZzB4OqXE4EgAPzJD6fBz4OabKxoA8Q";

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        YouTube youtubeService = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("youtube-cmdline-search-sample").build();

        YouTube.Search.List request = youtubeService.search()
                .list("snippet");
        SearchListResponse response = request.setKey(KEY)
                .setMaxResults(25L)
                .setQ("Слава Комиссаренко «Спасибо, у меня всё #2»")
                .execute();

        JsonObject jsonObject = new JsonParser()
                .parse(response.getItems().get(0).toString())
                .getAsJsonObject();

        String channelTitle = jsonObject.get("snippet")
                .getAsJsonObject()
                .get("channelTitle")
                .getAsString();

        String id = jsonObject.get("id")
                .getAsJsonObject()
                .get("videoId")
                .getAsString();


        System.out.println(channelTitle);
        System.out.println(id);


//        System.out.println(jsonObject.toString());

//        YouTube.Videos.List requestVideo = youtubeService
//                .videos()
//                .list()
    }
}
