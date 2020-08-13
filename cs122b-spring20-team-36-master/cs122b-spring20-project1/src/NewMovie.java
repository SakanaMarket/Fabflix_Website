import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "NewMovieServlet", urlPatterns = "/api/newmovie")
public class NewMovie extends HttpServlet {

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
//        System.out.println("Before params");
        String mName = request.getParameter( "m_title" );
        if( mName.equals("") ){
            responseJsonObject.addProperty( "status", "failure" );
            responseJsonObject.addProperty( "message", "failed to add new movie" );
            out.write( responseJsonObject.toString() );
            return;
        }
        String mYear = request.getParameter( "m_year" );
        String mDir = request.getParameter( "m_dir" );
        String starName = request.getParameter( "s_name" );
        String gen = request.getParameter( "genre" );
//        System.out.println(mName + " " + mYear + " " + mDir + " " + starName + " " + gen);
//        System.out.println("After get params");
        try{
            Connection dbcon = dataSource.getConnection();
            String query = "{call test(?,?,?,?,?)};";
            CallableStatement statement = dbcon.prepareCall( query );
//            System.out.println("After Call");
            statement.setString(1,mName);
            statement.setInt(2,Integer.parseInt(mYear));
            statement.setString(3,mDir);
            statement.setString(4,starName);
            statement.setString(5,gen);
            System.out.println(statement.toString());

            ResultSet rs = statement.executeQuery();
//            System.out.println("Executed");
            if( rs.next() ){
                String[] message = rs.getString("msg").split(" ");
                System.out.println(message);

                if (message[0].equals("new"))
                {
                    System.out.println(message);
                    responseJsonObject.addProperty("status", "success" );
                    responseJsonObject.addProperty("message", "Successfully added new movie: \n" +
                            "Movie Id: " + message[1] + "\n" +
                            "Genre Id: " + message[2] + "\n" +
                            "Star Id: " + message[3] + "\n");
                }
                else
                {
                    System.out.println(message);
                    responseJsonObject.addProperty("status", "Error" );
                    responseJsonObject.addProperty("message", "Movie already exists: " + message[1] );
                }

            }
            response.setStatus(200);
            dbcon.close();
            statement.close();
            rs.close();
            out.write( responseJsonObject.toString() );
//            System.out.println("After query");
        } catch (Exception e ){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            response.setStatus(500);
        }
        out.close();
    }
}