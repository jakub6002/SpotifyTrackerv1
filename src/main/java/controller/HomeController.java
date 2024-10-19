package controller;

import model.Artist;
import model.Release;
import model.Track;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import service.SpotifyService;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    private final SpotifyService spotifyService;

    @Autowired
    public HomeController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @GetMapping("/home")
    public String home(@RequestParam(value = "timeRange", required = false, defaultValue = "short_term") String timeRange,
                       OAuth2AuthenticationToken authenticationToken, Model model) {
        if (authenticationToken == null) {
            return "redirect:/login";
        }

        // Fetch user data using the selected time range
        List<Track> topTracks = spotifyService.getUserTopTracks(authenticationToken, timeRange);
        List<Artist> topArtists = spotifyService.getUserTopArtists(authenticationToken, timeRange);
        List<Track> recommendedTracks = spotifyService.getRecommendations(authenticationToken);
        List<Release> upcomingReleases = spotifyService.getUpcomingReleases(authenticationToken);  // Add this if missing

        // Add data to the model
        model.addAttribute("topTracks", topTracks);
        model.addAttribute("topArtists", topArtists);
        model.addAttribute("recommendedTracks", recommendedTracks);
        model.addAttribute("upcomingReleases", upcomingReleases);
        model.addAttribute("selectedTimeRange", timeRange);  // To remember selected time range in view
        model.addAttribute("authentication", authenticationToken);

        return "home";
    }

}
