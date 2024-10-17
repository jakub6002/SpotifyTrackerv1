package model;

public class Artist {
    private String name;

    // Getter and Setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Artist{name='" + name + "'}";
    }
}