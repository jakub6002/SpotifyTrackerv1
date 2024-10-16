package controller;

import org.springframework.ui.Model;

import model.Track;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import service.SpotifyService;

import java.util.List;

@Controller
public class SpotifyController {

    private final SpotifyService spotifyService;

    public SpotifyController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";  // Render custom login page
    }

    @GetMapping("/login/oauth2/code/spotify")
    public String handleSpotifyRedirect() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;

            spotifyService.printUserTopArtists(authToken);
            spotifyService.printUserTopTracks(authToken);
            return "redirect:/success";  // Redirect to success page after login
        } else {
            System.out.println("User is not authenticated.");
            return "error";  // Redirect to error page if not authenticated
        }
    }

    @GetMapping("/search")
    public String searchTracks(@RequestParam("query") String query, Model model, OAuth2AuthenticationToken authenticationToken) {
        List<Track> searchResults = spotifyService.searchTracks(query, authenticationToken);
        model.addAttribute("searchResults", searchResults);
        return "searchResults";
    }

}
