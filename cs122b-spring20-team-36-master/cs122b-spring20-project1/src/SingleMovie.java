import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
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

@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovie extends HttpServlet {
    private static final long serialVersionUID = 2L;

    @Resource( name = "jdbc/moviedb" )
    private DataSource dataSource;

    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {

        response.setContentType( "application/json" );
        String id = request.getParameter( "id" );
        PrintWriter out = response.getWriter();

        try{
            Connection dbcon = dataSource.getConnection();
            String query = "SELECT m.title, m.year, m.director, r.rating \n" +
                    "FROM movies m LEFT JOIN ratings r ON m.id = r.movieId \n" +
                    "WHERE m.id = ?;";
            PreparedStatement statement = dbcon.prepareStatement( query );
            statement.setString( 1, id );
            ResultSet rs = statement.executeQuery();
            ResultSet rs_temp;
            JsonObject jsonObject = new JsonObject();
            JsonArray castArray = new JsonArray();
            JsonArray genreArray = new JsonArray();

            if( rs.next() ){
                String movie_title = rs.getString( "title" );
                String movie_year = rs.getString( "year" );
                String movie_director = rs.getString( "director" );
                String movie_rating = rs.getString( "rating" );

                jsonObject.addProperty( "movie_title", movie_title );
                jsonObject.addProperty( "movie_year", movie_year );
                jsonObject.addProperty( "movie_director", movie_director );
                jsonObject.addProperty( "movie_rating", movie_rating );
                //jsonArray.add( jsonObject );

                query = "SELECT s.id, s.name \n" +
                        "FROM ( SELECT s.id, s.name FROM stars_in_movies sm, stars s WHERE sm.movieId = ? and sm.starId = s.id ) as p, movies as m, stars_in_movies sm, stars s \n" +
                        "WHERE s.id = p.id and m.id = sm.movieId and sm.starId = s.id \n" +
                        "GROUP BY s.id \n" +
                        "ORDER BY count(*) DESC, s.name ASC;";
                statement = dbcon.prepareStatement( query );
                statement.setString( 1, id );
                rs_temp = statement.executeQuery();

                while( rs_temp.next() ){
                    String star_id = rs_temp.getString( "id" );
                    String star_name = rs_temp.getString( "name" );

                    JsonObject tempObject = new JsonObject();
                    tempObject.addProperty("star_id", star_id );
                    tempObject.addProperty( "star_name", star_name );
                    castArray.add( tempObject );
                }
                //jsonArray.add( castArray );
                jsonObject.add( "stars", castArray );

                query = "SELECT g.id, g.name \n" +
                        "FROM movies m, genres_in_movies gm, genres g \n" +
                        "WHERE m.id = ? and m.id = gm.movieId and gm.genreId = g.id \n" +
                        "ORDER BY g.name ASC;";
                statement = dbcon.prepareStatement( query );
                statement.setString( 1, id );
                rs_temp = statement.executeQuery();

                while( rs_temp.next() ){
                    String genre_id = rs_temp.getString( "id" );
                    String genre_name = rs_temp.getString( "name" );
                    JsonObject tempObject = new JsonObject();
                    tempObject.addProperty( "genre_id", genre_id );
                    tempObject.addProperty( "genre_name", genre_name );
                    genreArray.add( tempObject );
                }
                //jsonArray.add( genreArray );
                jsonObject.add( "genres", genreArray );
                rs_temp.close();
            }
            out.write( jsonObject.toString() );
            response.setStatus( 200 );
            rs.close();
            statement.close();
            dbcon.close();

        } catch( Exception e ) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            response.setStatus(500);
        }
        out.close();
    }
}
