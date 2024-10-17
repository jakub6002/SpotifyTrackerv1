package controller;

import model.Artist;
import model.Track;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import service.SpotifyService;

import java.util.List;

@Controller
public class HomeController {

    private final SpotifyService spotifyService;

    @Autowired
    public HomeController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @GetMapping("/home")
    public String home(OAuth2AuthenticationToken authenticationToken, Model model) {
        if (authenticationToken == null) {
            return "redirect:/login";
        }

        // Fetch user top tracks and artists
        List<Track> topTracks = spotifyService.getUserTopTracks(authenticationToken);
        List<Artist> topArtists = spotifyService.getUserTopArtists(authenticationToken);

        // Add attributes to the model
        model.addAttribute("topTracks", topTracks);
        model.addAttribute("topArtists", topArtists);
        model.addAttribute("authentication", authenticationToken);

        // Render the home.html template
        return "home";
    }
}
