package com.example.multifunctionalchat.api;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class YouTubeApi {
    private static final String CLIENT_SECRETS = "src/main/resources/client_secret.json";
    private static final Collection<String> SCOPES =
            Collections.singletonList("https://www.googleapis.com/auth/youtube.readonly");
    private static final String APPLICATION_NAME = "YouTube API";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private YouTube youtubeService;

    public YouTubeApi() {
        try {
            youtubeService = getService();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public static Credential authorize(final NetHttpTransport httpTransport) throws IOException {
        InputStream in = new FileInputStream(CLIENT_SECRETS);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow
                .Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        return credential;
    }

    public static YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = authorize(httpTransport);
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }


    public String likeCount(String channelName, String videoName) throws IOException {
        YouTube.Videos.List request1 = youtubeService.videos().list(Collections.singletonList("statistics"))
                .setId(Collections.singletonList(getUrl(channelName, videoName)));
        VideoListResponse response1 = request1.execute();
        List<Video> results1 = response1.getItems();
        return "LikeCount = " + results1.get(0).getStatistics().getLikeCount();
    }

    public String viewCount(String channelName, String videoName) throws IOException {
        YouTube.Videos.List request1 = youtubeService.videos().list(Collections.singletonList("statistics"))
                .setId(Collections.singletonList(getUrl(channelName, videoName)));
        VideoListResponse response1 = request1.execute();
        List<Video> results1 = response1.getItems();
        return "ViewCount = " + results1.get(0).getStatistics().getViewCount();
    }

    public String getUrl(String channelName, String videoName) throws IOException {
        YouTube.Search.List requestChannel = youtubeService.search().list(Collections.singletonList("snippet"));
        SearchListResponse responseChannel = requestChannel.setQ(channelName)
                .setType(Collections.singletonList("channel"))
                .setMaxResults(5L)
                .execute();
        List<SearchResult> channelResult = responseChannel.getItems();

        String channelId = channelResult.get(0).getSnippet().getChannelId();

        YouTube.Search.List request = youtubeService.search().list(Collections.singletonList("snippet"))
                .setChannelId(channelId);
        SearchListResponse response = request.setQ(videoName)
                .setType(Collections.singletonList("video"))
                .setMaxResults(5L)
                .execute();
        SearchResult result = response.getItems().get(0);
        return "URL: https://www.youtube.com/watch?v=" + result.getId().getVideoId();
    }

    public String channelInfo(String channelName) throws IOException {
        YouTube.Search.List requestChannel = youtubeService.search().list(Collections.singletonList("snippet"));
        SearchListResponse responseChannel = requestChannel.setQ(channelName)
                .setType(Collections.singletonList("channel"))
                .setMaxResults(5L)
                .execute();
        SearchResult channelResult = responseChannel.getItems().get(0);
        String channelId = channelResult.getSnippet().getChannelId();
        StringBuilder builder = new StringBuilder();
        builder.append(channelResult.getSnippet().getChannelTitle());
        YouTube.Search.List request = youtubeService.search().list(Collections.singletonList("snippet"))
                .setChannelId(channelId);
        SearchListResponse response = request//.setQ(videoName)
                .setType(Collections.singletonList("video"))
                .setMaxResults(5L)
                .setOrder("date")
                .execute();
        List<SearchResult> results = response.getItems();
        for (SearchResult res: results) {
            builder.append(" https://www.youtube.com/watch?v=");
            builder.append(res.getId().getVideoId());
        }
        return builder.toString();
    }
}
