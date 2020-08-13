import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MovieSAXParser extends DefaultHandler {
    private String tempVal;
    private String tempDirector;
    private Movie tempMovie;

    private String movieIdString;
    private int nextMovieId;
    private int movieIdLength;

    private HashMap<String, Movie> movieDb;
    private HashMap<String, Integer> genreDb;
    private HashMap<String, String> genreCode;
    private HashMap<String, Movie> addedMovies;

    public MovieSAXParser(){
        movieDb = new HashMap<String, Movie>();
        addedMovies = new HashMap<String, Movie>();
        movieIdString = "tt";
        movieIdLength = 7;
        buildMovieDb();
        buildGenreDb();
        buildXMLGenreCoding();
    }

    private void buildMovieDb(){
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            Statement statement = connection.createStatement();

            String idQuery = "SELECT max(id) as id FROM movies;";
            ResultSet id = statement.executeQuery( idQuery );
            if( id.next() ){
                this.nextMovieId = Integer.parseInt( id.getString( "id" ).substring(2) )+1;
            }
            id.close();

            String movieQuery = "SELECT * FROM movies;";
            ResultSet movies = statement.executeQuery( movieQuery );
            while( movies.next() ){
                String movieId = movies.getString( "id" );
                String movieTitle = movies.getString( "title" );
                String movieYear = movies.getString( "year" );
                String movieDirector = movies.getString( "director" );
                this.movieDb.put( movieTitle+movieYear+movieDirector, new Movie( movieId, movieTitle, Integer.parseInt( movieYear ) , movieDirector ) );
            }
            movies.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println( "Error: " + e.toString() );
        }
    }
    private void buildGenreDb(){
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        try {
            genreDb = new HashMap<String, Integer>();
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            Statement statement = connection.createStatement();

            String genreQuery = "SELECT * FROM genres;";
            ResultSet genres = statement.executeQuery( genreQuery );
            while( genres.next() ){
                String genreId = genres.getString( "id" );
                String genreName = genres.getString( "name" );
                this.genreDb.put( genreName,  Integer.parseInt( genreId ) );
            }
            genres.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println( "Error: " + e.toString() );
        }
    }
    private void updateGenreDb( String genreName ){
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

            String genreQuery = "INSERT INTO genres (name) VALUES (?);";
            PreparedStatement statement = connection.prepareStatement( genreQuery );
            statement.setString( 1, genreName );
            statement.executeUpdate();
            String alterQuery = "ALTER TABLE genres AUTO_INCREMENT=1;";
            statement = connection.prepareStatement( alterQuery );
            statement.executeUpdate();

            statement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println( "Error: " + e.toString() );
        }
    }
    private void loadGenreAndMovieFile(){
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

            String movies = "movies.csv";
            String genresInMovies = "genresInMovies.csv";
            String ratings = "ratings.csv";

            String update = "LOAD DATA LOCAL INFILE ? INTO TABLE movies " +
                                    "FIELDS TERMINATED BY ',' " +
                                    "LINES TERMINATED BY '\n';";
            PreparedStatement statement = connection.prepareStatement( update );
            statement.setString( 1, movies );
            statement.executeUpdate();

            update = "LOAD DATA LOCAL INFILE ? INTO TABLE genres_in_movies " +
                    "FIELDS TERMINATED BY ',' " +
                    "LINES TERMINATED BY '\n';";
            statement = connection.prepareStatement( update );
            statement.setString( 1, genresInMovies );
            statement.executeUpdate();

            update = "LOAD DATA LOCAL INFILE ? INTO TABLE ratings " +
                    "FIELDS TERMINATED BY ',' " +
                    "LINES TERMINATED BY '\n';";
            statement = connection.prepareStatement( update );
            statement.setString( 1, ratings );
            statement.executeUpdate();

            statement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println( "Error: " + e.toString() );
        }
    }
    private void createFile( String filename ){
        try{
            File newFile = new File( filename );
            newFile.createNewFile();
        } catch( Exception e ){ }
    }
    private void appendToFile( String filename, String message ){
        try{
            FileWriter out = new FileWriter( filename, true );
            out.write( message + "\n" );
            out.close();
        } catch( Exception e ){ }
    }
    private void removeFiles( String filename ){
        try{
            File newFile = new File( filename );
            newFile.delete();
        } catch( Exception e ){ }
    }
    public boolean contains( String filmId ){
        if( this.addedMovies.containsKey( filmId ) ){  return true; }
        return false;
    }
    public Movie get( String filmId ){ return this.addedMovies.get( filmId ); }


    /* For debugging
    private void printMovieDb(){
        System.out.println( "Next Movie Id " + nextMovieId );
        for( Movie m : movieDb.values() ){
            System.out.println( m.toString() );
        }
    }
    private void printGenreDb(){
        System.out.println( "Next Genre Id " + nextGenreId );
        genreDb.entrySet().forEach( entry-> {
            System.out.println("Genre Name: " + entry.getKey() + ", Genre ID: " + entry.getValue() );
        });
    }
    public void getAddedMovies(){
        addedMovies.entrySet().forEach( entry->{
           System.out.println( entry.getKey() + " " + entry.getValue().toString() );
        });
    }*/

    private void buildXMLGenreCoding(){
        genreCode = new HashMap<String,String>();
        genreCode.put( "Susp", "Thriller" );
        genreCode.put( "CnR", "Cops and Robbers" );
        genreCode.put( "Dram", "Drama" );
        genreCode.put( "West", "Western" );
        genreCode.put( "Myst", "Mystery" );
        genreCode.put( "S.F.", "Sci-Fi" );
        genreCode.put( "Advt", "Adventure" );
        genreCode.put( "Horr", "Horror" );
        genreCode.put( "Romt", "Romance" );
        genreCode.put( "Comd", "Comedy" );
        genreCode.put( "Musc", "Musical" );
        genreCode.put( "Docu", "Documentary" );
        genreCode.put( "Porn", "Pornography" );
        genreCode.put( "Noir", "Noir" );
        genreCode.put( "BioP", "Biography" );
        genreCode.put( "TV", "TV" );
        genreCode.put( "TVs", "TV" );
        genreCode.put( "TVm", "TV" );
    }
    private String combineMovieId(){
        return String.format( "%s%0" + movieIdLength + "d", movieIdString, nextMovieId++ );
    }

    public void characters( char[] ch, int start, int length ) throws SAXException{
        tempVal = new String( ch, start, length );
    }
    private boolean verifyMovie( Movie m ){
        if (m.getDirector().equals("")) {
            appendToFile("report.txt", (m.toString() + "; Director is not defined") );
            return false;
        } else if (m.getYear() == 0) {
            appendToFile("report.txt", (m.toString() + "; Year is not defined") );
            return false;
        } else if (m.getTitle().equals("")) {
            appendToFile("report.txt", (m.toString() + "; Title is not defined") );
            return false;
        } else if (this.movieDb.containsKey(m.getTitle() + m.getYear() + m.getDirector())) {
            m.setId( this.movieDb.get( m.getTitle()+m.getYear()+m.getDirector() ).getId() );
            addedMovies.put( tempMovie.getFilm_id(), m );
            appendToFile("report.txt", (m.toString() + "; Duplicate movie") );
            return false;
        }
        return true;
    }
    public void startElement( String uri, String localName, String qName, Attributes attributes ) throws SAXException {
        tempVal = "";
        if( qName.equalsIgnoreCase("directorfilms" ) ){
            tempDirector = "";
        } else if( qName.equalsIgnoreCase( "film") ){
            tempMovie = new Movie();
        }
    }
    public void endElement( String uri, String localName, String qName ) throws SAXException {
        if( qName.equalsIgnoreCase( "dirname" ) ){
            tempDirector = tempVal;
        } else if( qName.equalsIgnoreCase("film" ) ){
            tempMovie.setDirector( tempDirector );
            if( verifyMovie( tempMovie ) ){
                tempMovie.setId( combineMovieId() );
                appendToFile( "movies.csv", tempMovie.toString() );
                appendToFile( "ratings.csv", tempMovie.getId() + ",0,0" );
                ArrayList<Integer> temp = tempMovie.getGenres();
                for( int i = 0; i < temp.size(); ++i ){
                    appendToFile( "genresInMovies.csv", temp.get(i) + "," + tempMovie.getId() );
                }
                addedMovies.put( tempMovie.getFilm_id(), tempMovie );
            }
        } else if( qName.equalsIgnoreCase( "year") ){
            try {
                tempMovie.setYear( Integer.parseInt(tempVal) );
            }
            catch( Exception e ){
                tempMovie.setYear( 0 );
            }
        } else if( qName.equalsIgnoreCase( "t" ) ){
            tempMovie.setTitle( tempVal );
        }
        else if( qName.equalsIgnoreCase( "cat" ) ){
            if( genreCode.containsKey( tempVal ) ){
                String genreName = genreCode.get( tempVal );
                if( !genreDb.containsKey( genreName ) ){
                    updateGenreDb( genreName );
                    buildGenreDb();
                }
                tempMovie.addGenre( genreDb.get( genreName ) );
            }
        }
        else if ( qName.equalsIgnoreCase("fid")){
            tempMovie.setFilm_id( tempVal );
        }
    }
    public void runExample(){
        System.out.println("Parsing movie file\n");
        removeFiles( "movies.csv" );
        removeFiles( "genresInMovies.csv" );
        removeFiles( "ratings.csv" );
        removeFiles( "report.txt" );
        createFile( "movies.csv" );
        createFile( "genresInMovies.csv" );
        createFile( "ratings.csv" );
        createFile( "report.txt" );
        parseDocument();
        loadGenreAndMovieFile();
    }
    private void parseDocument(){
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try{
            SAXParser sp = spf.newSAXParser();
            sp.parse( "mains243.xml", this );
        } catch( SAXException se ){
            se.printStackTrace();
        } catch( ParserConfigurationException pce ){
            pce.printStackTrace();
        } catch( IOException ie ){
            ie.printStackTrace();
        }
    }

    public static void main( String[] args ){
        MovieSAXParser msp = new MovieSAXParser();
        msp.runExample();
    }
}
