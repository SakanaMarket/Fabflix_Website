
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

public class MovieStarSAXParser extends DefaultHandler {

    private String tempVal;
    private String tempFilmId;
    private String tempStageName;

    MovieSAXParser msp;
    StarSAXParser ssp;

    private HashMap<String, Integer> movieStarsDb;

    public MovieStarSAXParser( ){
        msp = new MovieSAXParser();
        msp.runExample();
        ssp = new StarSAXParser();
        ssp.runExample();
        buildMovieStarsDb();
    }

    private void buildMovieStarsDb(){
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        try {
            movieStarsDb = new HashMap<String, Integer>();
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

            String movieStarQuery = "SELECT * FROM stars_in_movies;";
            PreparedStatement statement = connection.prepareStatement( movieStarQuery );
            ResultSet movieStars = statement.executeQuery();
            while( movieStars.next() ){
                String starId = movieStars.getString( "starId" );
                String movieId = movieStars.getString( "movieId" );
                this.movieStarsDb.put( starId+movieId, 1 );
            }

            statement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println( "Error: " + e.toString() );
        }
    }
    private void loadMovieStarFile(){
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

            String movieStars = "moviestars.csv";

            String update = "LOAD DATA LOCAL INFILE ? INTO TABLE stars_in_movies " +
                    "FIELDS TERMINATED BY ',' " +
                    "LINES TERMINATED BY '\n';";
            PreparedStatement statement = connection.prepareStatement( update );
            statement.setString( 1, movieStars );
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
    /*public void printMovieStarsDb(){
        movieStarsDb.entrySet().forEach( entry->{
            System.out.println( entry.getKey() + " "  + entry.getValue() );
        });
    }*/

    public void characters( char[] ch, int start, int length ) throws SAXException{
        tempVal = new String( ch, start, length );
    }

    public void startElement( String uri, String localName, String qName, Attributes attributes ) throws SAXException{
        tempVal = "";
    }

    public void endElement( String uri, String localName, String qName ) throws SAXException{
        if( qName.equalsIgnoreCase( "f" ) ){
            tempFilmId = tempVal;
        } else if( qName.equalsIgnoreCase( "a" ) ){
            tempStageName = tempVal;
        } else if( qName.equalsIgnoreCase( "m" ) ){
            if( msp.contains( tempFilmId ) && ssp.contains( tempStageName ) ){
                Movie m = msp.get( tempFilmId );
                Star s = ssp.get( tempStageName );
                if( !movieStarsDb.containsKey( s.getId()+m.getId() ) ){
                    appendToFile( "moviestars.csv", s.getId() + "," + m.getId() );
                } else {
                    appendToFile( "report.txt", String.format("Film ID: %s, Film Name: %s already has actor %s", m.getFilm_id(), m.getTitle(), s.getName() ) );
                }
            }
        }
    }

    private void parseDocument(){
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try{
            SAXParser sp = spf.newSAXParser();
            sp.parse( "casts124.xml", this );
        } catch( SAXException se ){
            se.printStackTrace();
        } catch( ParserConfigurationException pce ){
            pce.printStackTrace();
        } catch( IOException ie ){
            ie.printStackTrace();
        }
    }

    public void runExample(){
        System.out.println( "Parsing movie star file\n");
        removeFiles( "moviestars.csv" );
        createFile( "moviestars.csv" );
        parseDocument();
        loadMovieStarFile();
        removeFiles("movies.csv" );
        removeFiles("genresInMovies.csv" );
        removeFiles("stars.csv" );
        removeFiles("moviestars.csv");
    }

    public static void main( String[] args ){
        MovieStarSAXParser mssp = new MovieStarSAXParser();
        mssp.runExample();
    }
}
