package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Artist;
import model.Track;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class SpotifyService {

    private final WebClient webClient;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    public SpotifyService(WebClient.Builder webClientBuilder, OAuth2AuthorizedClientService authorizedClientService) {
        this.webClient = webClientBuilder.baseUrl("https://api.spotify.com/v1").build();
        this.authorizedClientService = authorizedClientService;
    }

    private OAuth2AuthorizedClient getAuthorizedClient(OAuth2AuthenticationToken authenticationToken) {
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                authenticationToken.getAuthorizedClientRegistrationId(),
                authenticationToken.getName());

        if (authorizedClient == null) {
            System.out.println("No authorized client found. Authentication failed or client not properly registered.");
        }

        return authorizedClient;
    }

    private String getAccessToken(OAuth2AuthorizedClient authorizedClient) {
        if (authorizedClient != null) {
            return authorizedClient.getAccessToken().getTokenValue();
        }
        return null;
    }

    public List<Track> getUserTopTracks(OAuth2AuthenticationToken authenticationToken) {
        System.out.println("Fetching user top tracks...");

        OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(authenticationToken);
        String accessToken = getAccessToken(authorizedClient);

        if (accessToken == null) {
            return List.of();
        }

        String response = webClient.get()
                .uri("/me/top/tracks")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        List<Track> tracks = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.get("items");

            if (items != null) {
                for (JsonNode item : items) {
                    Track track = new Track();
                    track.setName(item.get("name").asText());
                    if (item.has("artists") && item.get("artists").isArray()) {
                        JsonNode artistNode = item.get("artists").get(0);
                        track.setArtistName(artistNode.get("name").asText());
                    }
                    tracks.add(track);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!tracks.isEmpty()) {
            System.out.println("Successfully retrieved " + tracks.size() + " top tracks from Spotify API.");
        } else {
            System.out.println("No top tracks found or failed to retrieve top tracks from Spotify API.");
        }

        return tracks;
    }

    public List<Artist> getUserTopArtists(OAuth2AuthenticationToken authenticationToken) {
        System.out.println("Fetching user top artists...");

        OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(authenticationToken);
        String accessToken = getAccessToken(authorizedClient);

        if (accessToken == null) {
            return List.of();
        }

        String response = webClient.get()
                .uri("/me/top/artists")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        List<Artist> artists = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.get("items");

            if (items != null) {
                for (JsonNode item : items) {
                    Artist artist = new Artist();
                    artist.setName(item.get("name").asText());
                    artists.add(artist);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!artists.isEmpty()) {
            System.out.println("Successfully retrieved " + artists.size() + " top artists from Spotify API.");
        } else {
            System.out.println("No top artists found or failed to retrieve top artists from Spotify API.");
        }

        return artists;
    }

    public List<Track> searchTracks(String query, OAuth2AuthenticationToken authenticationToken) {
        System.out.println("Searching tracks with query: " + query);

        OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(authenticationToken);
        String accessToken = getAccessToken(authorizedClient);

        if (accessToken == null) {
            return List.of();
        }

        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("q", query)
                        .queryParam("type", "track")
                        .build())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        List<Track> tracks = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.get("tracks").get("items");

            if (items != null) {
                for (JsonNode item : items) {
                    Track track = new Track();
                    track.setName(item.get("name").asText());
                    if (item.has("artists") && item.get("artists").isArray()) {
                        JsonNode artistNode = item.get("artists").get(0);
                        track.setArtistName(artistNode.get("name").asText());
                    }
                    tracks.add(track);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!tracks.isEmpty()) {
            System.out.println("Successfully retrieved " + tracks.size() + " tracks from search query: " + query);
        } else {
            System.out.println("No tracks found or failed to retrieve tracks from search query: " + query);
        }

        return tracks;
    }

    public void printUserTopTracks(OAuth2AuthenticationToken authenticationToken) {
        List<Track> tracks = getUserTopTracks(authenticationToken);
        if (!tracks.isEmpty()) {
            System.out.println("Top Tracks:");
            tracks.forEach(track -> System.out.println(track.getName() + " by " + track.getArtistName()));
        } else {
            System.out.println("No tracks found.");
        }
    }
}
