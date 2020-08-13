import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "NewStarServlet", urlPatterns = "/api/newstar")
public class NewStar extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedbMaster")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType( "application/json" );
        PrintWriter out = response.getWriter();
        JsonObject responseJsonObject = new JsonObject();

        String starName = request.getParameter( "star_name" );
        if( starName.equals("") ){
            responseJsonObject.addProperty( "status", "failure" );
            responseJsonObject.addProperty( "message", "failed to add new star" );
            out.write( responseJsonObject.toString() );
            return;
        }
        String birthYear = request.getParameter( "birth_year" );
        try{
            Connection dbcon = dataSource.getConnection();
            String query = "SELECT max(id) as id FROM stars;";
            PreparedStatement statement = dbcon.prepareStatement( query );
            ResultSet rs = statement.executeQuery();
            if( rs.next() ){
                String newID = "nm" + Integer.parseInt( rs.getString( "id" ).substring( 2 ) )+1;
                String update = "INSERT INTO stars( id, name, birthYear ) VALUES ( ?, ?, ? )";
                statement = dbcon.prepareStatement( update );
                statement.setString( 1, newID );
                statement.setString( 2, starName );
                if( birthYear.equals("") ){
                    statement.setNull( 3, java.sql.Types.INTEGER );
                } else {
                    statement.setString(3, birthYear);
                }
                statement.executeUpdate();
                responseJsonObject.addProperty("status", "success" );
                responseJsonObject.addProperty("message", "successfully added new star: " + newID );
            }
            response.setStatus(200);
            dbcon.close();
            statement.close();
            rs.close();
            out.write( responseJsonObject.toString() );
        } catch (Exception e ){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            response.setStatus(500);
        }
        out.close();
    }
}