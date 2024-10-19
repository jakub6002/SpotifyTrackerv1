package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Artist;
import model.Release;
import model.Track;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return authorizedClientService.loadAuthorizedClient(
                authenticationToken.getAuthorizedClientRegistrationId(),
                authenticationToken.getName());
    }

    public void printUserTopTracks(OAuth2AuthenticationToken authenticationToken) {
        String timeRange = "short_term"; // Default time range
        List<Track> tracks = getUserTopTracks(authenticationToken, timeRange);

        if (!tracks.isEmpty()) {
            System.out.println("Top Tracks:");
            tracks.forEach(track -> System.out.println(track.getName() + " by " + track.getArtistName()));
        } else {
            System.out.println("No tracks found.");
        }
    }

    public List<Track> getUserTopTracks(OAuth2AuthenticationToken authenticationToken, String timeRange) {
        OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(authenticationToken);
        String accessToken = getAccessToken(authorizedClient);

        if (accessToken == null) {
            return List.of();  // Return an empty list if the token is not available
        }

        // Call Spotify Top Tracks API
        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/me/top/tracks")
                        .queryParam("time_range", timeRange)
                        .build())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Parse the top tracks from the API response
        return parseTracks(response);
    }


    public List<Artist> getUserTopArtists(OAuth2AuthenticationToken authenticationToken, String timeRange) {
        OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(authenticationToken);
        String accessToken = authorizedClient.getAccessToken().getTokenValue();

        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/me/top/artists")
                        .queryParam("time_range", timeRange)
                        .build())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return parseArtists(response);
    }

    private List<Track> parseTracks(String jsonResponse) {
        List<Track> tracks = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode items = root.get("items");

            if (items != null) {
                for (JsonNode item : items) {
                    Track track = new Track();
                    track.setName(item.get("name").asText());

                    // Set artist name and ID
                    if (item.has("artists") && item.get("artists").isArray()) {
                        JsonNode artistNode = item.get("artists").get(0);
                        track.setArtistName(artistNode.get("name").asText());
                        track.setArtistId(artistNode.get("id").asText());
                    }

                    // Set album image URL
                    if (item.has("album") && item.get("album").has("images") && item.get("album").get("images").isArray()) {
                        JsonNode imageNode = item.get("album").get("images").get(0);
                        track.setAlbumImageUrl(imageNode.get("url").asText());
                    }

                    // Set Spotify URL and popularity
                    track.setSpotifyUrl(item.get("external_urls").get("spotify").asText());
                    track.setPlayCount(item.get("popularity").asInt());

                    tracks.add(track);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tracks;
    }


    private List<Artist> parseArtists(String jsonResponse) {
        List<Artist> artists = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode items = root.get("items");

            if (items != null) {
                for (JsonNode item : items) {
                    Artist artist = new Artist();
                    artist.setName(item.get("name").asText());
                    if (item.has("images") && item.get("images").isArray()) {
                        JsonNode imageNode = item.get("images").get(0);
                        artist.setImageUrl(imageNode.get("url").asText());
                    }
                    artist.setPopularity(item.get("popularity").asInt());
                    artists.add(artist);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return artists;
    }

    private String getAccessToken(OAuth2AuthorizedClient authorizedClient) {
        if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
            return authorizedClient.getAccessToken().getTokenValue();
        }
        return null;
    }

    public Map<String, Double> getTrackMoodFeatures(String trackId, OAuth2AuthenticationToken authenticationToken) {
        OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(authenticationToken);
        String accessToken = authorizedClient.getAccessToken().getTokenValue();

        // Call Spotify Audio Features API
        String response = webClient.get()
                .uri("/audio-features/" + trackId)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Parse the audio features from the API response
        Map<String, Double> features = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);

            features.put("energy", root.get("energy").asDouble());
            features.put("danceability", root.get("danceability").asDouble());
            features.put("valence", root.get("valence").asDouble());  // Mood positivity
            features.put("tempo", root.get("tempo").asDouble());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return features;
    }

    public List<Track> getMoodBasedRecommendations(String mood, OAuth2AuthenticationToken authenticationToken) {
        OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(authenticationToken);
        String accessToken = authorizedClient.getAccessToken().getTokenValue();

        // Define mood-based valence
        String genreSeed = "pop";  // Can be customized
        String targetValence;

        switch (mood.toLowerCase()) {
            case "happy":
                targetValence = "0.8";
                break;
            case "chill":
                targetValence = "0.4";
                break;
            default:
                targetValence = "0.6";  // Default for 'energetic'
                break;
        }

        // Call Spotify Recommendations API
        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/recommendations")
                        .queryParam("seed_genres", genreSeed)
                        .queryParam("target_valence", targetValence)
                        .queryParam("limit", "10")
                        .build())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Parse the track data from the API response
        return parseTracks(response);
    }


    public List<Release> getUpcomingReleases(OAuth2AuthenticationToken authenticationToken) {
        OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(authenticationToken);
        String accessToken = getAccessToken(authorizedClient);

        if (accessToken == null) {
            return List.of();  // Return an empty list if there's no access token
        }

        String response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/browse/new-releases")
                        .queryParam("limit", 10)
                        .build())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        List<Release> releases = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);
            JsonNode albums = root.get("albums").get("items");

            for (JsonNode album : albums) {
                String title = album.get("name").asText();
                String releaseDate = album.get("release_date").asText();

                JsonNode artists = album.get("artists");
                String artistName = artists.get(0).get("name").asText();

                releases.add(new Release(title, artistName, releaseDate));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return releases;
    }

    public List<Track> searchTracks(String query, OAuth2AuthenticationToken authenticationToken) {
        OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(authenticationToken);
        String accessToken = getAccessToken(authorizedClient);

        if (accessToken == null) {
            return List.of();  // Return an empty list if the token is null
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
                    track.setSpotifyUrl(item.get("external_urls").get("spotify").asText());
                    tracks.add(track);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tracks;
    }

    public List<Track> getRecommendations(OAuth2AuthenticationToken authenticationToken) {
        OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(authenticationToken);
        String accessToken = authorizedClient.getAccessToken().getTokenValue();

        String response = webClient.get()
                .uri("/recommendations?seed_genres=pop,rock&limit=10")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return parseTracks(response);
    }

    public Map<String, Integer> getUserGenreDistribution(OAuth2AuthenticationToken authenticationToken) {
        List<Track> tracks = getUserTopTracks(authenticationToken, "long_term"); // Long term gives a larger dataset
        Map<String, Integer> genreDistribution = new HashMap<>();

        for (Track track : tracks) {
            String genre = getGenreForArtist(track.getArtistId(), authenticationToken);
            genreDistribution.put(genre, genreDistribution.getOrDefault(genre, 0) + 1);
        }

        return genreDistribution;
    }

    public Map<String, List<Track>> getUserTopTracksOverTime(OAuth2AuthenticationToken authenticationToken) {
        Map<String, List<Track>> topTracksOverTime = new HashMap<>();
        topTracksOverTime.put("short_term", getUserTopTracks(authenticationToken, "short_term"));
        topTracksOverTime.put("medium_term", getUserTopTracks(authenticationToken, "medium_term"));
        topTracksOverTime.put("long_term", getUserTopTracks(authenticationToken, "long_term"));

        return topTracksOverTime;
    }

    private String getGenreForArtist(String artistId, OAuth2AuthenticationToken authenticationToken) {
        OAuth2AuthorizedClient authorizedClient = getAuthorizedClient(authenticationToken);
        String accessToken = authorizedClient.getAccessToken().getTokenValue();

        String response = webClient.get()
                .uri("/artists/" + artistId)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);
            return root.get("genres").get(0).asText(); // Return the first genre
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown";
    }
}
