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
import com.google.api.services.youtube.model.VideoListResponse;
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
    private final String LINK_FORM = "https://www.youtube.com/watch?v=";

    private YouTube getAuthentication() {
        return new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName(APPLICATION_NAME).build();

    }

    public String get(String nameVideo, String nameChannel, boolean flagViews, boolean flagLikes) throws IOException {
        String id = null;
        String result = "Video not found";

        YouTube youtubeService = this.getAuthentication();

        YouTube.Search.List request = youtubeService.search()
                .list("snippet");
        SearchListResponse response = request.setKey(KEY)
                .setMaxResults(25L)
                .setQ(nameVideo)
                .execute();

        for (SearchResult res : response.getItems()) {
            JsonObject jsonObject = new JsonParser()
                    .parse(res.toString())
                    .getAsJsonObject();

            String findChannel = jsonObject.get("snippet")
                    .getAsJsonObject()
                    .get("channelTitle")
                    .getAsString();
            String findVideo = jsonObject.get("snippet")
                    .getAsJsonObject()
                    .get("title")
                    .getAsString();

            if (findVideo.equals(nameVideo) && findChannel.equals(nameChannel)) {
                id = jsonObject.get("id")
                        .getAsJsonObject()
                        .get("videoId")
                        .getAsString();
                break;
            }
        }

        if (id != null && !flagLikes && !flagViews)
            return result = LINK_FORM + id + "\n";
        if (id != null && flagLikes) {
            return result = LINK_FORM + id + "\n" + "Count likes: " + getLikes(youtubeService, id);
        } if (id != null && flagViews) {
            return result = LINK_FORM + id + "\n" + "Count likes: " + getViews(youtubeService, id);
        }

        return result;
    }

    private String getLikes(YouTube youtubeService, String id) throws IOException {
        YouTube.Videos.List requestVid = youtubeService.videos()
                .list("snippet,contentDetails,statistics");
        VideoListResponse responseVid = requestVid
                .setKey(KEY)
                .setPart("statistics")
                .setId(id)
                .execute();

        return (new JsonParser()
                .parse(responseVid.toString())
                .getAsJsonObject())
                .get("items")
                .getAsJsonObject()
                .get("statistics")
                .getAsJsonObject()
                .get("likeCount")
                .getAsString();

    }

    private String getViews(YouTube youtubeService, String id) throws IOException {
        YouTube.Videos.List requestVid = youtubeService.videos()
                .list("snippet,contentDetails,statistics");
        VideoListResponse responseVid = requestVid
                .setKey(KEY)
                .setPart("statistics")
                .setId(id)
                .execute();

        return (new JsonParser()
                .parse(responseVid.toString())
                .getAsJsonObject())
                .get("items")
                .getAsJsonObject()
                .get("statistics")
                .getAsJsonObject()
                .get("viewCount")
                .getAsString();

    }
}

