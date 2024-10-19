package service;

import model.Release;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReleaseService {

    public List<Release> getUpcomingReleases() {
        // Przykładowe dane, w rzeczywistości możesz pobierać te dane z API lub bazy danych
        List<Release> releases = new ArrayList<>();
        releases.add(new Release("New Album", "Artist 1", "2024-11-01"));
        releases.add(new Release("EP Release", "Artist 2", "2024-12-05"));
        releases.add(new Release("Single Release", "Artist 3", "2025-01-10"));

        return releases;
    }
}