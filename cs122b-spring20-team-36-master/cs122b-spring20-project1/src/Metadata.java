import com.google.gson.JsonArray;
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
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "MetadataServlet", urlPatterns = "/api/metadata")
public class Metadata extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType( "application/json" );
        PrintWriter out = response.getWriter();
        JsonArray jsonArray = new JsonArray();

        try{
            Connection dbcon = dataSource.getConnection();
            DatabaseMetaData metaData = dbcon.getMetaData();
            ResultSet tables = metaData.getTables( null, null, null, null );
            while( tables.next() ){
                JsonObject jsonObject = new JsonObject();
                JsonArray varArray = new JsonArray();
                String table = tables.getString( "Table_NAME" );
                ResultSet columns = metaData.getColumns( null, null, table, null );
                while( columns.next() ){
                    JsonObject temp = new JsonObject();
                    String field = columns.getString( "COLUMN_NAME" );
                    String type = columns.getString( "TYPE_NAME" ) + "(" + columns.getString( "COLUMN_SIZE") + ")";
                    temp.addProperty( "field", field );
                    temp.addProperty( "type", type );
                    varArray.add( temp );
                }
                jsonObject.addProperty( "tableName", table );
                jsonObject.add( "attributes", varArray );
                jsonArray.add( jsonObject );
                columns.close();
            }
            out.write( jsonArray.toString() );
            response.setStatus(200);
            dbcon.close();
            tables.close();

        } catch (Exception e ){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            response.setStatus(500);
        }
        out.close();
    }
}