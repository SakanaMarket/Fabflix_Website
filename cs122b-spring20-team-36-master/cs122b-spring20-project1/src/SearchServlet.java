import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet( name = "SearchServlet", urlPatterns = "/api/search" )
public class SearchServlet extends HttpServlet {

    @Resource( name = "jdbc/moviedb" )
    private DataSource dataSource;

    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

        long startServletTime = System.nanoTime();

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        JsonArray jsonArray = new JsonArray();
        String query = request.getParameter("query" );

        if( query == null || query.trim().isEmpty() ){
            out.write( jsonArray.toString() );
            return;
        }

        long elapsedJDBCTime = -1;

        try{
            long startJDBCTime = System.nanoTime();
            Connection dbcon = dataSource.getConnection();

            String movieQuery = "SELECT id, title FROM movies WHERE match(title) AGAINST ( ? IN BOOLEAN MODE ) LIMIT 10;";
            String[] keywords = query.split( " " );
            String match = "";
            for( int i = 0; i < keywords.length; ++i ) {
                match += "+" + keywords[i] + "*";
                if (i != keywords.length - 1) {
                    match += " ";
                }
            }
            PreparedStatement statement = dbcon.prepareStatement( movieQuery );
            statement.setString( 1, match );

            ResultSet rs = statement.executeQuery();
            long endJDBCTime = System.nanoTime();
            elapsedJDBCTime = endJDBCTime - startJDBCTime;

            while( rs.next() ){
                String movieId = rs.getString( "id" );
                String movieTitle = rs.getString( "title" );
                jsonArray.add( generateJsonObject( movieId, movieTitle) );
            }

            out.write( jsonArray.toString() );
            response.setStatus( 200 );

            startJDBCTime = System.nanoTime();
            rs.close();
            statement.close();
            dbcon.close();
            endJDBCTime = System.nanoTime();
            elapsedJDBCTime += endJDBCTime - startJDBCTime;

        }catch( Exception e ){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            response.setStatus( 500 );
        }
        out.close();

        long endServletTime = System.nanoTime();
        long elapsedServletTime = endServletTime - startServletTime;

        String contextPath = getServletContext().getRealPath("/");
        String xmlFilePath = contextPath+"logs.txt";
        System.out.println( xmlFilePath );

        File newFile = new File( xmlFilePath );
        newFile.createNewFile();

        FileWriter writer = new FileWriter( xmlFilePath, true );
        writer.write( elapsedServletTime + "," + elapsedJDBCTime + "\n" );
        writer.close();
    }

    private static JsonObject generateJsonObject( String movieId, String movieTitle ){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty( "value", movieTitle );

        JsonObject additionalJsonObject = new JsonObject();
        additionalJsonObject.addProperty( "movieId", movieId );

        jsonObject.add( "data", additionalJsonObject );
        return jsonObject;
    }

}
