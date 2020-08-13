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
import javax.xml.transform.Result;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.google.gson.JsonParser;

@WebServlet( name = "ConfirmationServlet", urlPatterns="/api/confirmation" )
public class ConfirmationServlet extends  HttpServlet{

    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        String customer_id = session.getAttribute("customer_id").toString();

        try{
            Connection dbcon = dataSource.getConnection();
            JsonArray transArray = (JsonArray) new JsonParser().parse(request.getParameter("trans_id"));
            JsonArray jsonArray = new JsonArray();
            String total_price = request.getParameter("cart_total");
            System.out.println(transArray.toString());

            for (int i = 0; i < transArray.size(); i++)
            {
                String sales_id = ((JsonObject) transArray.get(i)).get("sales_id").getAsString();

                String query = "SELECT s.id, s.quantity, m.title \n" +
                        "FROM movies m, sales s \n" +
                        "WHERE s.id = ? and s.movieId = m.id and s.customerId = ?;";

                PreparedStatement statement = dbcon.prepareStatement(query);
                statement.setString(1, sales_id);
                statement.setString(2, customer_id);

                ResultSet rs = statement.executeQuery();
                while (rs.next())
                {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("sale_id", rs.getString("id"));
                    jsonObject.addProperty("quantity", rs.getString("quantity"));
                    jsonObject.addProperty("title", rs.getString("title"));
                    jsonArray.add(jsonObject);
                }
                rs.close();
            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("total_price", total_price);
            jsonArray.add(jsonObject);

            out.write(jsonArray.toString());
            response.setStatus(200);
            dbcon.close();

            session.setAttribute("previousItems", null);
        } catch ( Exception e ){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            response.setStatus(500);
        }
        out.close();
    }


}
