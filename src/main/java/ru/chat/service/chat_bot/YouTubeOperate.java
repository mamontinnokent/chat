package ru.chat.service.chat_bot;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@AllArgsConstructor
public class YouTubeOperate {

    private final YouTube youTube;

    private final String KEY = "AIzaSyD30ZzB4OqXE4EgAPzJD6fBz4OabKxoA8Q";

    public String findVideoId(String videoName, String channelName) throws IOException {
        String id = null;
        List<SearchResult> foundVideos = this.findListVideosBy(videoName);

        for (SearchResult video : foundVideos) {
            String newVideoName = this.getVideoNameFrom(video);
            String newChannelName = this.getChannelNameFrom(video);

            if (newVideoName.equals(videoName) && newChannelName.equals(channelName)) {
                id = this.getVideoIdFrom(video);
                break;
            }
        }

        if (id == null)
            id = this.getVideoIdFrom(foundVideos.get(0));

        return id;
    }

    public String findChannelIdInYouTube(String channelName) throws IOException {
        String id = null;
        List<SearchResult> foundVideos = this.findListVideosBy(channelName);

        for (SearchResult video : foundVideos) {
            String newChannelName = this.getChannelNameFrom(video);

            if (newChannelName.equals(channelName)) {
                id = this.getChannelIdFrom(video);
                break;
            }
        }

        if (id == null)
            id = this.getChannelIdFrom(foundVideos.get(0));

        return id;
    }

    private String getChannelIdFrom(SearchResult video) {
        return new JsonParser()
                .parse(video.toString())
                .getAsJsonObject()
                .get("snippet")
                .getAsJsonObject()
                .get("channelId")
                .getAsString();
    }

    private String getVideoNameFrom(SearchResult video) {
        return new JsonParser()
                .parse(video.toString())
                .getAsJsonObject()
                .get("snippet")
                .getAsJsonObject()
                .get("title")
                .getAsString();
    }

    private String getChannelNameFrom(SearchResult video) {
        return new JsonParser()
                .parse(video.toString())
                .getAsJsonObject()
                .get("snippet")
                .getAsJsonObject()
                .get("channelTitle")
                .getAsString();
    }

    private List<SearchResult> findListVideosBy(String videoOrChannelName) throws IOException {
        return youTube.search()
                .list("snippet")
                .setKey(KEY)
                .setMaxResults(25L)
                .setQ(videoOrChannelName)
                .execute()
                .getItems();
    }

    private String getVideoIdFrom(SearchResult video) {
        return new JsonParser()
                .parse(video.toString())
                .getAsJsonObject()
                .get("snippet")
                .getAsJsonObject()
                .get("title")
                .getAsString();
    }

    public String getLikesBy(String id) throws IOException {
        VideoListResponse responseVid = youTube
                .videos()
                .list("snippet,contentDetails,statistics")
                .setKey(KEY)
                .setPart("statistics")
                .setId(id)
                .execute();

        return new JsonParser()
                .parse(responseVid.toString())
                .getAsJsonObject()
                .get("items")
                .getAsJsonObject()
                .get("statistics")
                .getAsJsonObject()
                .get("likeCount")
                .getAsString();

    }

    public String getViewsBy(String id) throws IOException {
        VideoListResponse responseVid = youTube
                .videos()
                .list("snippet,contentDetails,statistics")
                .setKey(KEY)
                .setPart("statistics")
                .setId(id)
                .execute();

        return new JsonParser()
                .parse(responseVid.toString())
                .getAsJsonObject()
                .get("items")
                .getAsJsonObject()
                .get("statistics")
                .getAsJsonObject()
                .get("viewCount")
                .getAsString();

    }
}

