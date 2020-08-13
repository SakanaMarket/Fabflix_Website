package edu.uci.ics.fabflixmobile;

public class Star {

    private String star_id;
    private String star_name;

    public String getId(){ return star_id; }
    public String getName(){ return star_name; }

    public String toString(){
        return "id: " + getId() + " name: " + getName() + "\n";
    }
}
