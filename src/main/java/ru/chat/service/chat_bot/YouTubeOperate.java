package ru.chat.service.chat_bot;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.VideoListResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
        return video.getSnippet().getChannelId();
    }

    private String getVideoNameFrom(SearchResult video) {
        return video.getSnippet().getTitle();
    }

    private String getChannelNameFrom(SearchResult video) {
        return video.getSnippet().getChannelTitle();
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
        return video.getId().getVideoId();
    }

    public String getLikesBy(String id) throws IOException {
        VideoListResponse responseVid = youTube
                .videos()
                .list("statistics")
                .setKey(KEY)
                .setPart("statistics")
                .setId(id)
                .execute();

        return responseVid.getItems()
                .get(0)
                .getStatistics()
                .getLikeCount()
                .toString();
    }

    public String getViewsBy(String id) throws IOException {
        VideoListResponse response = youTube
                .videos()
                .list("snippet,contentDetails,statistics")
                .setKey(KEY)
                .setPart("statistics")
                .setId(id)
                .execute();

        return response.getItems()
                .get(0)
                .getStatistics()
                .getViewCount()
                .toString();
    }

    public String findPlaylistId(String channelId) throws IOException {
        ChannelListResponse response = youTube.channels()
                .list("contentDetails")
                .setId(channelId)
                .setKey(KEY)
                .execute();
        
        return response.getItems()
                .get(0)
                .getContentDetails()
                .getRelatedPlaylists()
                .getUploads();
    }

    public List<String> findArrIdBy(String lastVidPlaylistId) throws IOException {
       List<PlaylistItem> response = youTube.playlistItems()
                .list("contentDetails")
                .setMaxResults(5L)
                .setKey(KEY)
                .setPlaylistId("UUxqkOxQYocXRtSqlotgXh7w")
                .execute()
                .getItems();

       return response.stream()
               .map(video -> video.getContentDetails().getVideoId())
               .collect(Collectors.toList());
    }
}

