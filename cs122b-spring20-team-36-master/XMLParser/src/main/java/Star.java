import java.util.ArrayList;

public class Star {
    private String id = "";
    private String name = "";
    private String year = "";
    private ArrayList<String> movies = new ArrayList<>();

    public Star(){
    }

    public Star(String id, String name, String year){
        this.id = id;
        this.name = name;
        this.year = year;
    }

    public String getId(){ return id; }
    public void setId( String id ){ this.id = id; }
    public String getName(){ return name; }
    public void setName( String name ){ this.name = name; }
    public String getYear(){ return year; }
    public void setYear( String year ){ this.year = year; }

    public ArrayList<String> getMovies(){ return movies; }
    public void addMovies( String m ){ movies.add( m ); }
    public void removeAllMovies(){ movies.clear(); }

    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append( getId() + "," + getName() + "," );
        sb.append( getYear() );
        return sb.toString();
    }

}
