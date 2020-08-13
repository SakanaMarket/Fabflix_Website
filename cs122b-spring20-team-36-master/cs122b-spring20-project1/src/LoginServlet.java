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

import org.jasypt.util.password.StrongPasswordEncryptor;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JsonObject responseJsonObject = new JsonObject();
        String mobile = request.getParameter( "mobile" );

        if( mobile != null && !mobile.equals("1") ) {
            String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
            try {
                RecaptchaVerifyUtils.verify(gRecaptchaResponse);
            } catch (Exception e) {
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", e.getMessage());
                response.setStatus(200);
                out.write(responseJsonObject.toString());
                out.close();
                return;
            }
        }

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Declare our statement

            String query = "SELECT *\n" +
                    "FROM customers\n" +
                    "WHERE email = ?"; //" and password = ?\n";

            PreparedStatement statement = dbcon.prepareStatement(query);

            statement.setString(1, email);
            //statement.setString(2, password);

            ResultSet rs = statement.executeQuery();

            if (rs.next())
            {
                String encryptedPassword = rs.getString("password");

                // use the same encryptor to compare the user input password with encrypted password stored in DB
                boolean success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
                if (success)
                {
                    System.out.println((encryptedPassword));
                    System.out.println("verify " + email + " - " + password);
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                    request.getSession().setAttribute("session", new Session( rs.getString( "email") ) );
                    request.getSession().setAttribute( "customer_id", Integer.parseInt( rs.getString("id") ) );
                }
                else
                {
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "user doesn't exist or incorrect password");
                }

            }
            else
            {
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "user doesn't exist or incorrect password");
            }

            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            statement.close();
            dbcon.close();

            out.write(responseJsonObject.toString());

        } catch (Exception e) {

            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        out.close();
    }
}
