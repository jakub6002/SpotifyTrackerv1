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
        System.out.println("Login page accessed");
        return "login";  // Render custom login page
    }

    @GetMapping("/success")
    public String successPage() {
        System.out.println("Success page acessed");
        return "success";  // Zwraca widok success.html
    }





    @GetMapping("/search")
    public String searchTracks(@RequestParam("query") String query, Model model, OAuth2AuthenticationToken authenticationToken) {
        List<Track> searchResults = spotifyService.searchTracks(query, authenticationToken);
        model.addAttribute("searchResults", searchResults);
        return "searchResults";
    }


}
