package model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Track {

    private String name;
    private String artist;

    public Track(){}


    public Track(String name, String artist) {
        this.name = name;
        this.artist = artist;
    }


}
