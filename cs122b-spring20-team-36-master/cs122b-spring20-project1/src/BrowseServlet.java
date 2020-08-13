import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

@WebServlet(name = "BrowseMovieServlet", urlPatterns = "/api/browse" )
public class BrowseServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    @Resource( name = "jdbc/moviedb" )
    private DataSource dataSource;

    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

        response.setContentType( "application/json" );
        PrintWriter out = response.getWriter();

        try{
            Connection dbcon = dataSource.getConnection();
            String query = "SELECT * FROM genres\n" +
                    "ORDER BY name ASC;";

            Statement statement = dbcon.createStatement();
            ResultSet rs = statement.executeQuery( query );
            JsonArray jsonArray = new JsonArray();

            while ( rs.next() ){
                String genre_id = rs.getString( "id" );
                String genre_name = rs.getString( "name" );

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty( "genre_id", genre_id );
                jsonObject.addProperty( "genre_name", genre_name );

                jsonArray.add( jsonObject );
            }
            out.write( jsonArray.toString() );
            response.setStatus( 200 );
            rs.close();
            statement.close();
            dbcon.close();

        } catch( Exception e ) {
            //System.out.println(e.getMessage());
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            response.setStatus(500);
        }
        out.close();
    }
}

