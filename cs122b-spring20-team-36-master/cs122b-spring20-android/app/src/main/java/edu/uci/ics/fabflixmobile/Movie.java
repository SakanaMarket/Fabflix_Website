package edu.uci.ics.fabflixmobile;

import java.util.ArrayList;

public class Movie {
    private String movie_id;
    private String movie_title;
    private String movie_year;
    private String movie_director;
    private String movie_rating;
    private ArrayList<Genre> genres;
    private ArrayList<Star> stars;


    public String getId(){ return movie_id; }
    public String getTitle(){ return movie_title; }
    public String getYear(){ return movie_year; }
    public String getDirector(){ return movie_director; }
    public String getRating(){ return movie_rating; }
    public ArrayList<Genre> getGenres(){ return genres; }
    public ArrayList<Star> getStars(){ return stars; }

    public String toString(){
        return "id: " + getId() + " title: " + getTitle() + " Year: " + getYear() + " director: " + getDirector() + " rating: " + getRating() + "\n";
    }
}