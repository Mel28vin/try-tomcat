import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
@WebServlet("/api/v1/customers/*")
public class CustomerController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            out.print(CustomerUtils.getAllCustomers());
        } else {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                String idStr = pathParts[1];
                int customer_id = 0;
                try {
                    customer_id = Integer.parseInt(idStr);
                } catch (NumberFormatException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
                    return;
                }
                out.print(CustomerUtils.getCustomer(customer_id));
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL");
            }
        }
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
            StringBuilder jsonInput = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                jsonInput.append(line);
            }

            String jsonData = jsonInput.toString();

            System.out.println("Received JSON data: " + jsonData);

            boolean isSucess = CustomerUtils.addCustomer(jsonData);

            if (isSucess)
                out.write("{ \"status\": \"success\", \"message\": \"JSON data received successfully\" }");
            else
                out.write("{ \"status\": \"error\", \"message\": \"Error in DB\" }");
        } catch (Exception e) {
            e.printStackTrace();
            out.write("{ \"status\": \"error\", \"message\": \"Error processing JSON data\" }");
        }
    }
}