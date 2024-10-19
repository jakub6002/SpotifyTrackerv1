package controller;

import model.Track;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import service.SpotifyService;

import java.util.List;
import java.util.Map;

@Controller
public class SpotifyController {

    private final SpotifyService spotifyService;

    public SpotifyController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @GetMapping("/search")
    public String searchTracks(@RequestParam("query") String query, Model model, OAuth2AuthenticationToken authenticationToken) {
        List<Track> searchResults = spotifyService.searchTracks(query, authenticationToken);
        model.addAttribute("searchResults", searchResults);
        return "searchResults";
    }

    @GetMapping("/genre-distribution")
    public ResponseEntity<Map<String, Integer>> getGenreDistribution(OAuth2AuthenticationToken authenticationToken) {
        // Corrected call to retrieve genre distribution
        Map<String, Integer> genreDistribution = spotifyService.getUserGenreDistribution(authenticationToken);
        if (genreDistribution != null) {
            return ResponseEntity.ok(genreDistribution);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/top-tracks-over-time")
    public ResponseEntity<Map<String, List<Track>>> getTopTracksOverTime(OAuth2AuthenticationToken authenticationToken) {
        // Correctly assign the topTracksOverTime variable
        Map<String, List<Track>> topTracksOverTime = spotifyService.getUserTopTracksOverTime(authenticationToken);

        if (topTracksOverTime != null) {
            return ResponseEntity.ok(topTracksOverTime);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
