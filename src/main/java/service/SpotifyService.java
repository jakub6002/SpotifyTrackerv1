package service;

import model.Track;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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

    public List<Track> getUserTopTracks(OAuth2AuthenticationToken authenticationToken) {
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                authenticationToken.getAuthorizedClientRegistrationId(),
                authenticationToken.getName());

        if (authorizedClient == null) {
            System.out.println("No authorized client found for the user.");
            return List.of();
        }

        String accessToken = authorizedClient.getAccessToken().getTokenValue();

        return webClient.get()
                .uri("/me/top/tracks")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToFlux(Track.class)
                .collectList()
                .block();
    }

    public void printUserTopTracks(OAuth2AuthenticationToken authenticationToken) {
        List<Track> tracks = getUserTopTracks(authenticationToken);
        if (!tracks.isEmpty()) {
            tracks.forEach(track -> System.out.println(track.getName() + " by " + track.getArtist()));
        } else {
            System.out.println("No tracks found or user is not authenticated.");
        }
    }
}
