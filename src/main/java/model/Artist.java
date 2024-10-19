package model;

public class Artist {
    private String name;
    private String imageUrl;
    private String spotifyUrl;
    private int popularity;  // Add popularity field

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSpotifyUrl() {
        return spotifyUrl;
    }

    public void setSpotifyUrl(String spotifyUrl) {
        this.spotifyUrl = spotifyUrl;
    }

    public int getPopularity() {
        return popularity;  // Add getter for popularity
    }

    public void setPopularity(int popularity) {  // Add setter for popularity
        this.popularity = popularity;
    }

    @Override
    public String toString() {
        return "Artist{name='" + name + "', imageUrl='" + imageUrl + "', spotifyUrl='" + spotifyUrl + "', popularity=" + popularity + "}";
    }
}
