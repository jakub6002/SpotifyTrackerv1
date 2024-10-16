package org.spotifyconsole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import service.SpotifyService;

@SpringBootApplication
@ComponentScan(basePackages = {"org.spotifyconsole", "controller", "model", "service"})
public class SpotifyConsoleApplication implements CommandLineRunner {

    @Autowired
    private SpotifyService spotifyService;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    public static void main(String[] args) {
        SpringApplication.run(SpotifyConsoleApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
            spotifyService.printUserTopTracks(authToken);
        } else {
            System.out.println("User is not authenticated. Cannot retrieve Spotify data.");
        }
    }
}
