package ru.chat.service.chat_bot;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class YouTubeOperate {

    private final YouTube youTube;

    private final String KEY = "AIzaSyD30ZzB4OqXE4EgAPzJD6fBz4OabKxoA8Q";

    public String findVideoIdBy(String videoName, String channelName) throws IOException {
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
                .setPlaylistId(lastVidPlaylistId)
                .execute()
                .getItems();

       return response.stream()
               .map(video -> video.getContentDetails().getVideoId())
               .collect(Collectors.toList());
    }

    public List<String> getComment(String videoId) throws IOException {
        var result = "";
        var videoCommentsListResponse = getCommentsThreadBy(videoId);

        int countPages = videoCommentsListResponse.getPageInfo().getTotalResults();
        int countAllComments = countPages * 20;

        int randomNbrComment = new Random().nextInt(countAllComments);
        long page = randomNbrComment / countPages;
        int video = (int) (randomNbrComment % 20);


        if (page != 0) {
            int counter = 0;

            while (true) {
                counter += 1;
                String token = videoCommentsListResponse.getNextPageToken();
                videoCommentsListResponse = getCommentsThreadBy(videoId, token);

                if (counter == page)
                    break;
            }
        }

        var comments = videoCommentsListResponse.getItems();
        if (video > comments.size()) {
            var comment = comments.get(comments.size() - 1)
                    .getSnippet()
                    .getTopLevelComment();
            var author = comment.getSnippet().getAuthorDisplayName();
            var content= comment.getSnippet().getTextDisplay();

            return List.of(author, content);
        } else {
            var comment = comments.get(video)
                    .getSnippet()
                    .getTopLevelComment();
            var author = comment.getSnippet().getAuthorDisplayName();
            var content= comment.getSnippet().getTextDisplay();

            return List.of(author, content);
        }
    }

    private CommentThreadListResponse getCommentsThreadBy(String videoId) throws IOException {
        return youTube.commentThreads()
                .list("snippet")
                .setKey(KEY)
                .setVideoId(videoId)
                .setTextFormat("plainText")
                .execute();
    }

    private CommentThreadListResponse getCommentsThreadBy(String videoId, String pageToken) throws IOException {
        return youTube.commentThreads()
                .list("snippet")
                .setKey(KEY)
                .setVideoId(videoId)
                .setPageToken(pageToken)
                .setTextFormat("plainText")
                .execute();
    }
}

