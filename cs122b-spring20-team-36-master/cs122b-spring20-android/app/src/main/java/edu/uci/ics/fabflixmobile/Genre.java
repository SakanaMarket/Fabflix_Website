package edu.uci.ics.fabflixmobile;

public class Genre {

    private String genre_id;
    private String genre_name;

    public String getId(){ return genre_id; }
    public String getName(){ return genre_name; }

    public String toString(){
        return "id: " + getId() + " name: " + getName() + "\n";
    }

}
