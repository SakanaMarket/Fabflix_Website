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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@WebServlet( name = "PaymentServlet", urlPatterns="/api/payment" )
public class PaymentServlet extends HttpServlet{

    @Resource(name = "jdbc/moviedbMaster")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try{
            Connection dbcon = dataSource.getConnection();
            JsonArray jsonArray = new JsonArray();
            HttpSession session = request.getSession();

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
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession( true );
        HashMap<String,Integer> previousItems = (HashMap<String,Integer>) session.getAttribute( "previousItems" );

        String firstName = request.getParameter( "first_name" );
        String lastName = request.getParameter( "last_name" );
        String ccNum = request.getParameter( "credit_card_num" );
        String expDate = request.getParameter( "expiration_date" );

        try{
            Connection dbcon = dataSource.getConnection();
            JsonArray jsonArray = new JsonArray();
            JsonObject jsonObject = new JsonObject();
            if( previousItems == null || previousItems.isEmpty() ){
                jsonObject.addProperty( "status", "failure" );
                jsonObject.addProperty( "message", "no items in shopping cart" );
                jsonArray.add( jsonObject );
            }
            else {
                String query = "SELECT * FROM creditcards where id = ? and firstName = ? and lastName = ? and expiration = ?;";
                PreparedStatement statement = dbcon.prepareStatement(query);
                statement.setString(1, ccNum);
                statement.setString(2, firstName);
                statement.setString(3, lastName);
                statement.setString(4, expDate);
                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    jsonObject.addProperty("status", "success");
                    jsonObject.addProperty("message", "transaction success");
                    jsonArray.add( jsonObject );

                    JsonArray transArray = new JsonArray();
                    for( Map.Entry element: previousItems.entrySet() ){
                        String k = (String) element.getKey();
                        int v = (int) element.getValue();
                        try {
                            String customerId = session.getAttribute("customer_id").toString();
                            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                            String sale = "INSERT INTO sales( customerId, movieId, saleDate, quantity ) VALUES ( ?, ?, ?, ? );";
                            PreparedStatement saleStatement = dbcon.prepareStatement(sale);

                            saleStatement.setString(1, customerId);
                            saleStatement.setString(2, k);
                            saleStatement.setString(3, date);
                            saleStatement.setInt(4, v );

                            int rs_state = saleStatement.executeUpdate();
                            if( rs_state == 1 ){
                                System.out.println("Sucessfully added new sale");
                            }

                            String retrieve = "SELECT s.id FROM sales s WHERE s.customerId = ? and s.movieId = ? and s.saleDate = ? and s.quantity = ?;";
                            saleStatement = dbcon.prepareStatement(retrieve);
                            saleStatement.setString(1, customerId);
                            saleStatement.setString(2, k);
                            saleStatement.setString(3, date);
                            saleStatement.setInt(4, v );

                            ResultSet rs_trans = saleStatement.executeQuery();
                            if (rs_trans.next()) {
                                JsonObject transObject = new JsonObject();
                                transObject.addProperty("sales_id", rs_trans.getString("id"));
                                transArray.add(transObject);
                            }
                            rs_trans.close();

                        } catch (Exception e) {
                            JsonObject tempObject = new JsonObject();
                            tempObject.addProperty("errorMessage", e.getMessage());
                            out.write(tempObject.toString());
                            response.setStatus(500);
                        }

                    }
                    jsonArray.add( transArray );
                } else {
                    jsonObject.addProperty("status", "failure");
                    jsonObject.addProperty("message", "incorrect payment information");
                    jsonArray.add(jsonObject);
                }
                rs.close();
            }
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
}
