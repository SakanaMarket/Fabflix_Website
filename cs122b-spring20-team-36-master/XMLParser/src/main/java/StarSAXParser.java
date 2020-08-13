
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

public class StarSAXParser extends DefaultHandler{
    private String tempVal;
    private Star tempStar;

    private String starIdSubstring;
    private int starIdLength;
    private int nextStarId;

    private HashMap<String, Star> starDb;
    private HashMap<String, Star> addedStars;

    public StarSAXParser(){
        addedStars = new HashMap<String, Star>();
        starIdSubstring = "nm";
        starIdLength = 7;
        buildStarDb();
    }

    private void buildStarDb(){
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        try {
            starDb = new HashMap<String, Star>();
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
            Statement statement = connection.createStatement();

            String idQuery = "SELECT max(id) as id FROM stars;";
            ResultSet id = statement.executeQuery( idQuery );
            if( id.next() ){
                nextStarId = Integer.parseInt( id.getString( "id" ).substring(2) ) + 1;
            }
            id.close();

            String starQuery = "SELECT * FROM stars;";
            ResultSet stars = statement.executeQuery( starQuery );
            while( stars.next() ){
                String starId = stars.getString( "id" );
                String starName = stars.getString( "name" );
                String birthYear = stars.getString( "birthYear" );
                if( birthYear == null ) {
                    birthYear = "";
                }
                this.starDb.put( starName+birthYear, new Star( starId, starName, birthYear ) );
            }
            stars.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println( "Error: " + e.toString() );
        }
    }
    private void loadStarFile(){
        String loginUser = "mytestuser";
        String loginPasswd = "mypassword";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);

            String stars = "stars.csv";

            String update = "LOAD DATA LOCAL INFILE ? INTO TABLE stars " +
                    "FIELDS TERMINATED BY ',' " +
                    "LINES TERMINATED BY '\n' " +
                    "( id, name, @birthYear) " +
                    "SET birthYear = NULLIF( @birthYear, '' );";
            PreparedStatement statement = connection.prepareStatement( update );
            statement.setString( 1, stars );
            statement.executeUpdate();

            statement.close();
            connection.close();
        } catch (Exception e) {
            System.out.println( "Error: " + e.toString() );
        }
    }
    private String combineStarId(){
        return String.format( "%s%0" + starIdLength + "d", starIdSubstring, nextStarId++ );
    }

    /*public void printStarDb(){
        System.out.println( "Next Star ID " + combineStarId() );
        starDb.entrySet().forEach( entry->{
           System.out.println( entry.getKey() + " " + entry.getValue().toString() );
        });
    }
    public void printAddedStars(){
        addedStars.entrySet().forEach( entry->{
            System.out.println( entry.getKey() + " " + entry.getValue().toString() );
        });
    }*/

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
    private boolean verifyStar( Star s ){
        if( s.getName().equals("") ){
            appendToFile( "report.txt", s.toString()+"; Star name is not defined" );
            return false;
        } else if( this.starDb.containsKey( s.getName()+s.getYear() ) ){
            appendToFile( "report.txt", s.toString()+"; Duplicate star" );
            s.setId( this.starDb.get( s.getName()+s.getYear() ).getId() );
            addedStars.put( s.getName(), s );
            return false;
        }
        return true;
    }

    public boolean contains( String stageName ){
        if( this.addedStars.containsKey( stageName ) ){ return true; }
        return false;
    }
    public Star get( String stageName ){ return this.addedStars.get( stageName ); }


    public void characters( char[] ch, int start, int length ) throws SAXException{
        tempVal = new String( ch, start, length );
    }

    public void startElement( String uri, String localName, String qName, Attributes attributes ) throws SAXException{
        tempVal = "";
        if( qName.equalsIgnoreCase("actor") ){
            tempStar = new Star();
        }
    }
    public void endElement( String uri, String localName, String qName ) throws SAXException {
        if( qName.equalsIgnoreCase( "actor" ) ){
            if( verifyStar( tempStar) ){
                tempStar.setId( combineStarId() );
                appendToFile( "stars.csv", tempStar.toString() );
                addedStars.put( tempStar.getName(), tempStar );
            }
        }
        else if( qName.equalsIgnoreCase( "stagename") ){
            tempStar.setName( tempVal );
        } else if( qName.equalsIgnoreCase( "dob" ) ){
            tempStar.setYear( tempVal );
        }
    }

    private void parseDocument(){
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try{
            SAXParser sp = spf.newSAXParser();
            sp.parse( "actors63.xml", this );
        } catch( SAXException se ){
            se.printStackTrace();
        } catch( ParserConfigurationException pce ){
            pce.printStackTrace();
        } catch( IOException ie ){
            ie.printStackTrace();
        }
    }
    public void runExample(){
        System.out.println("Parsing star file\n");
        removeFiles( "stars.csv" );
        createFile( "stars.csv" );
        parseDocument();
        loadStarFile();
    }

}
