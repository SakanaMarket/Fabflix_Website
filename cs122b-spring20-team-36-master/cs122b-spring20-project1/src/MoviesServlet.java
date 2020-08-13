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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "StarsServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession( true );
        List<String> parameters = Arrays.asList("movie_title", "movie_year", "movie_director", "star_name", "genre_id", "char_id", "sort", "position", "page", "N");
        String clearSession = request.getParameter( "clear" );
        if( clearSession != null && !clearSession.equals("null") ){
            for( int i = 0; i < parameters.size(); ++i ){
                session.setAttribute( parameters.get(i), null );
            }
        }

        Enumeration<String> params = request.getParameterNames();
        while( params.hasMoreElements() ){
            String paramName = params.nextElement();
            String paramVal = request.getParameter( paramName );
            if( !paramName.equals("clear") && paramVal != null && !paramVal.equals("null") ){
                session.setAttribute( paramName, (String) request.getParameter( paramName ) );
            }
        }

        String m_title = (String) session.getAttribute("movie_title");
        String m_year = (String) session.getAttribute("movie_year");
        String m_dir = (String) session.getAttribute("movie_director");
        String s_name = (String) session.getAttribute("star_name");
        String g_id = (String) session.getAttribute("genre_id");
        String c_id = (String) session.getAttribute("char_id");
        String sort_param = (String) session.getAttribute("sort");
        String sort_pos = (String) session.getAttribute("position");
        String pg = (String) session.getAttribute("page");
        String n_movies = (String) session.getAttribute("N");

        String search[] = { m_year, m_dir, s_name };

        try {
            Connection dbcon = dataSource.getConnection();

            String query = "SELECT m.id, m.title, m.year, m.director, r.rating \n" +
                    "FROM movies m LEFT JOIN ratings r ON m.id = r.movieId, stars_in_movies sm, stars s, genres_in_movies gm, genres g \n" +
                    "WHERE m.id = r.movieId and m.id = sm.movieId and sm.starId = s.id and gm.movieId = m.id and gm.genreId = g.id\n" +
                    "and m.year LIKE ? and m.director LIKE ? and s.name LIKE ? \n" +
                    "and m.title REGEXP ? and m.title LIKE ? \n";

            if( m_title != null && !m_title.equals("null") && !m_title.isEmpty() ){
                query += "and match( m.title ) AGAINST ( ? in BOOLEAN mode ) \n";
            }

            if ( g_id != null && !g_id.equals("null")) {
                query += "and g.id = ? \n";
            }
            if ( sort_pos == null || sort_pos.equals("null") ) {
                sort_pos = "DESC";
            }
            query += "GROUP BY m.id, m.title, r.rating \n";
            if ( sort_param != null && sort_param.equals("title") && !sort_param.equals("null")) {
                if( sort_pos.equals( "DESC") ) {
                    query += "ORDER BY m.title DESC, r.rating DESC \n";
                } else {
                    query += "ORDER BY m.title ASC, r.rating ASC \n";
                }
            } else {
                if( sort_pos.equals( "DESC") ) {
                    query += "ORDER BY r.rating DESC, m.title DESC \n";
                } else {
                    query += "ORDER BY r.rating ASC, m.title ASC \n";
                }
            }
            query += "LIMIT ? \n";
            query += "OFFSET ?;";

            int index = 1;

            PreparedStatement statement = dbcon.prepareStatement(query);
            for( int i = 0; i < search.length; ++i ){
                if( search[i] != null && !search.equals("null") && !search[i].isEmpty() ){
                    statement.setString( index++, "%"+search[i]+"%" );
                } else {
                    statement.setString( index++, "%%" );
                }
            }
            if( c_id != null && !c_id.equals("null")){
                if( c_id.equals("*") ){
                    statement.setString( index++, "^[^a-zA-Z0-9]" );
                    statement.setString( index++, "%%" );
                } else {
                    statement.setString( index++, ".*" );
                    statement.setString( index++, c_id+"%" );
                }
            } else {
                statement.setString( index++,".*" );
                statement.setString( index++, "%%" );
            }

            if( m_title != null && !m_title.equals("null") && !m_title.isEmpty() ) {
                String[] keywords = m_title.split(" ");
                String match = "";
                for (int i = 0; i < keywords.length; i++) {
                    match += "+" + keywords[i] + "*";
                    if (i != keywords.length - 1) {
                        match += " ";
                    }
                }
                statement.setString( index++, match );
            }

            if( g_id != null && !g_id.equals("null")){
                statement.setString( index++, g_id );
            }
            int n_limit = 21;
            if( n_movies != null && !n_movies.equals("null")){
                n_limit = Integer.parseInt( n_movies ) + 1;
            }
            statement.setInt( index++, n_limit );
            int offset = 0;
            if( pg != null && !pg.equals("null")){
                offset = Integer.parseInt(pg) * (n_limit-1);
            }
            statement.setInt( index++, offset );


            System.out.println( statement.toString() );

            // Perform the query
            ResultSet rs = statement.executeQuery();
            ResultSet rsTemp;

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while ( rs.next() ) {
                //JsonArray tempArray = new JsonArray();
                JsonArray castArray = new JsonArray();
                JsonArray genreArray = new JsonArray();

                String movie_id = rs.getString( "id" );
                String movie_title = rs.getString( "title" );
                String movie_year = rs.getString( "year" );
                String movie_director = rs.getString( "director" );
                String movie_rating = rs.getString( "rating" );

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id );
                jsonObject.addProperty("movie_title", movie_title );
                jsonObject.addProperty( "movie_year", movie_year );
                jsonObject.addProperty( "movie_director", movie_director );
                jsonObject.addProperty( "movie_rating", movie_rating );
                //tempArray.add( jsonObject );

                query = "SELECT g.id, g.name \n" +
                        "FROM genres_in_movies gm, genres as g \n" +
                        "WHERE gm.movieId = ? and gm.genreId = g.id \n";

                query += "GROUP BY g.id ORDER BY g.name ASC LIMIT 3;";
                statement = dbcon.prepareStatement( query );
                statement.setString( 1, movie_id );
                rsTemp = statement.executeQuery();

                while( rsTemp.next() ){
                    JsonObject tempJsonObject = new JsonObject();
                    tempJsonObject.addProperty( "genre_id", rsTemp.getString( "id" ) );
                    tempJsonObject.addProperty( "genre_name", rsTemp.getString( "name" ) );
                    genreArray.add( tempJsonObject );
                }
                jsonObject.add( "genres", genreArray );
                //tempArray.add( genreArray );

                query = "SELECT s.id, s.name \n" +
                        "FROM ( SELECT s.id, s.name FROM stars_in_movies sm, stars s WHERE sm.movieId = ? and sm.starId = s.id ) as p, stars_in_movies sm, stars s \n" +
                        "WHERE s.id = p.id and sm.starId = s.id \n";

                query += "GROUP BY s.id \n" +
                        "ORDER BY count(*) DESC, s.name ASC LIMIT 3";
                statement = dbcon.prepareStatement( query );
                statement.setString( 1, movie_id );
                rsTemp = statement.executeQuery();

                while( rsTemp.next() ){
                    JsonObject tempJsonObject = new JsonObject();
                    tempJsonObject.addProperty( "star_id", rsTemp.getString( "id" ) );
                    tempJsonObject.addProperty( "star_name", rsTemp.getString( "name") );
                    castArray.add( tempJsonObject );
                }
                //better if we had made a single json object instead a json array
                jsonObject.add( "stars", castArray );
                //tempArray.add( castArray );
                jsonArray.add( jsonObject );
                //jsonArray.add( tempArray );
                rsTemp.close();
            }
            
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            statement.close();
            dbcon.close();
        } catch (Exception e) {
        	
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);

        }
        out.close();

    }
}
