import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

@WebServlet( name = "ShoppingCartServlet", urlPatterns="/api/shoppingcart" )
public class ShoppingCart extends HttpServlet{

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try{
            Connection dbcon = dataSource.getConnection();
            JsonArray jsonArray = new JsonArray();
            HttpSession session = request.getSession();

            HashMap<String,Integer> previousItems = (HashMap<String,Integer>) session.getAttribute( "previousItems" );
            if( previousItems == null ){
                previousItems = new HashMap<>();
            }
            int totalPrice = 0;

            for( Map.Entry e : previousItems.entrySet() ){
                String k = (String)e.getKey();
                int v = (int)e.getValue();
                String query = "SELECT m.id, m.title, p.price FROM movies m, prices p WHERE m.id = ? and m.id = p.movieId";

                PreparedStatement statement = dbcon.prepareStatement(query);
                statement.setString( 1, k );
                ResultSet rs = statement.executeQuery();

                while( rs.next() ) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("movie_id", rs.getString("id"));
                    jsonObject.addProperty("movie_title", rs.getString("title"));
                    jsonObject.addProperty("movie_price", Integer.parseInt(rs.getString("price")));
                    jsonObject.addProperty("movie_quantity", v);
                    jsonArray.add(jsonObject);
                    totalPrice += v * Integer.parseInt(rs.getString("price"));
                }
                rs.close();
                statement.close();
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty( "total_price", totalPrice );
            jsonArray.add( jsonObject );
            out.write(jsonArray.toString());
            response.setStatus(200);
            dbcon.close();

        } catch ( Exception e ){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            response.setStatus(500);
        }
        out.close();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession( true );
        HashMap<String,Integer> previousItems = (HashMap<String,Integer>) session.getAttribute( "previousItems" );
        if( previousItems == null ){
            previousItems = new HashMap<>();
        }

        String movieId = request.getParameter( "id" );
        int quantity = Integer.parseInt( request.getParameter( "quantity" ) );

        synchronized( previousItems ){
            if( movieId != null ){
                if( previousItems.get( movieId ) == null ){
                    previousItems.put( movieId, quantity );
                } else {
                    previousItems.put(movieId, previousItems.get(movieId) + quantity);
                }
                if( previousItems.get(movieId) <= 0 ){
                    previousItems.remove( movieId );
                }
            }
            session.setAttribute( "previousItems", previousItems );
        }

    }
}
