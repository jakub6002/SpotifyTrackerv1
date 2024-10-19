package model;

public class Release {
    private String title;
    private String artist;
    private String releaseDate;

    public Release(String title, String artist, String releaseDate) {
        this.title = title;
        this.artist = artist;
        this.releaseDate = releaseDate;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}