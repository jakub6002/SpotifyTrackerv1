package controller;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpotifyController {

    @GetMapping("/login")
    public String login() {
        return "redirect:/oauth2/authorization/spotify";
    }

    @GetMapping("/callback")
    public String callback(OAuth2AuthenticationToken authentication) {
        System.out.println("User details: " + authentication.getPrincipal().getAttributes());
        return "redirect:/";
    }
}
