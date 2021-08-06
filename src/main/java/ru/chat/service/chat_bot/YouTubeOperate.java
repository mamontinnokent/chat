package ru.chat.service.chat_bot;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class YouTubeOperate {

    private final JsonFactory JSON_FACTORY = new JacksonFactory();
    private final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    private final String KEY = "AIzaSyD30ZzB4OqXE4EgAPzJD6fBz4OabKxoA8Q";
    private final String APPLICATION_NAME = "youtube-cmdline-search-sample";

    public String getId(String searchingVideo, String searchingChannel) throws IOException {
        String id = null;

        YouTube youtubeService = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName(APPLICATION_NAME).build();

        YouTube.Search.List request = youtubeService.search()
                .list("snippet");
        SearchListResponse response = request.setKey(KEY)
                .setMaxResults(25L)
                .setQ(searchingVideo)
                .execute();

        JsonObject jsonObject = new JsonParser()
                .parse(response.getItems().get(0).toString())
                .getAsJsonObject();

        for (SearchResult res : response.getItems()) {
            String nameChannel = jsonObject.get("snippet")
                    .getAsJsonObject()
                    .get("channelTitle")
                    .getAsString();
            String nameVideo = jsonObject.get("snippet")
                    .getAsJsonObject()
                    .get("title")
                    .getAsString();

            if (searchingVideo.equals(nameVideo) && searchingChannel.equals(nameChannel)) {
                id = jsonObject.get("id")
                        .getAsJsonObject()
                        .get("videoId")
                        .getAsString();
                break;
            }
        }

        return id == null ? null : id;
    }

}

//    YouTube.Videos.List requestVid = youtubeService.videos()
//            .list("snippet,contentDetails,statistics");
//    VideoListResponse responseVid = requestVid
//            .setKey(KEY)
//            .setPart("statistics")
//            .setId(id)
//            .execute();
