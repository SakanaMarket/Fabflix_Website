import java.util.ArrayList;

public class Movie {
    private String id = "";
    private String title = "";
    private int year = 0;
    private String director = "";
    private String film_id = "";
    private ArrayList<Integer> genres = new ArrayList<Integer>();

    public Movie(){
    }

    public Movie( String id, String title, int year, String director ){
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
    }

    private String genreString(){
        String genreString = "";
        if( this.genres.size() > 0 ){
            genreString += "Genres="+this.genres.get(0);
            for( int i = 1; i < this.genres.size(); ++i ){
                genreString += ","+this.genres.get(i);
            }
        } else {
            genreString += "Genres=None";
        }
        return genreString;
    }

    public String getId(){ return id; }
    public void setId( String id ){ this.id = id; }
    public String getTitle(){ return title; }
    public void setTitle( String title ){ this.title = title; }
    public int getYear(){ return year; }
    public void setYear( int year ){ this.year = year; }
    public String getDirector(){ return director; }
    public void setDirector( String director ){ this.director = director; }
    public String getFilm_id(){ return film_id; }
    public void setFilm_id( String film_id ){ this.film_id = film_id; }

    public ArrayList<Integer> getGenres(){ return genres; }
    public void addGenre( int g ){ genres.add( g ); }
    public void removeAllGenres(){ genres.clear(); }

    public boolean isEquals( Movie m ){
        if( this.title.equals( m.title ) && this.year == m.year && this.director.equals( m.director ) ){
            return true;
        }
        return false;
    }

    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append( getId() + "," + getTitle() + "," );
        sb.append( getYear() + "," + getDirector() );
        return sb.toString();
    }

}
